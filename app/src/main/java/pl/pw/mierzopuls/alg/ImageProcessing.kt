package pl.pw.mierzopuls.alg

import android.annotation.SuppressLint
import android.graphics.ImageFormat
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import org.opencv.android.OpenCVLoader
import org.opencv.core.*
import org.opencv.core.Core.countNonZero
import org.opencv.core.Core.meanStdDev
import org.opencv.imgproc.Imgproc
import java.util.concurrent.Executors
import java.util.function.DoubleBinaryOperator

class ImageProcessing {
    var value: Int = -100

    companion object {
        const val LOG_TAG = "ImgProc"

        init {
            if (!OpenCVLoader.initDebug()) {
                Log.e(LOG_TAG, "Unable to load OpenCV! BE")
                throw InstantiationException("OpenCV not loaded correctly!")
            } else {
                Log.d(LOG_TAG, "OpenCV library loaded correctly")
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun imageAnalysisUseCase(onImage: (Image) -> Unit): ImageAnalysis {
        return ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //TODO: change to queue ?
            .build()
            .apply {
                setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                    imageProxy.use {
                        onImage(it.image!!)
                    }
                }
            }
    }

    fun processImage(algState: AlgState, image: Image): Double {
        val mat = image.yuvToRgba()
        return when (algState) {
            is AlgState.Register -> {
                val threshold = algState.calibration.getThreshold(2)
                Core.inRange(mat, threshold.first, threshold.second, mat)

                countNonZero(mat).toDouble()
            }
            is AlgState.Calibrate -> {
                mean(mat).`val`[0]
            }
            AlgState.NONE,
            is AlgState.Result,
            AlgState.DEBUG -> throw IllegalStateException("Algorithm cannot be $algState.")
        }
    }

    /**
     * @return List:
     * 0 -> red
     * 1 -> green
     * 2 -> blue
     */
    fun getRGBStats(image: Image): List<Double> {
        val mat = image.yuvToRgba()
        val mean = mean(mat)
        return listOf(
            mean.`val`[0],
            mean.`val`[1],
            mean.`val`[2],
        )
    }

    /**
     * @return Scalar:
     * val[0] -> red
     * val[1] -> green
     * val[2] -> blue
     */
    private fun mean(src: Mat): Scalar = Core.mean(src)

    /**
     * Converts YUV image to RGB matrix
     */
    private fun Image.yuvToRgba(): Mat {
        val rgbaMat = Mat()

        if (format == ImageFormat.YUV_420_888
            && planes.size == 3
        ) {
            val chromaPixelStride = planes[1].pixelStride

            if (chromaPixelStride == 2) { // Chroma channels are interleaved
                assert(planes[0].pixelStride == 1)
                assert(planes[2].pixelStride == 2)
                val yPlane = planes[0].buffer
                val uvPlane1 = planes[1].buffer
                val uvPlane2 = planes[2].buffer
                val yMat = Mat(height, width, CvType.CV_8UC1, yPlane)
                val uvMat1 = Mat(height / 2, width / 2, CvType.CV_8UC2, uvPlane1)
                val uvMat2 = Mat(height / 2, width / 2, CvType.CV_8UC2, uvPlane2)
                val addrDiff = uvMat2.dataAddr() - uvMat1.dataAddr()
                if (addrDiff > 0) {
                    assert(addrDiff == 1L)
                    Imgproc.cvtColorTwoPlane(yMat, uvMat1, rgbaMat, Imgproc.COLOR_YUV2RGBA_NV12)
                } else {
                    assert(addrDiff == -1L)
                    Imgproc.cvtColorTwoPlane(yMat, uvMat2, rgbaMat, Imgproc.COLOR_YUV2RGBA_NV21)
                }
            } else { // Chroma channels are not interleaved
                val yuvBytes = ByteArray(width * (height + height / 2))
                val yPlane = planes[0].buffer
                val uPlane = planes[1].buffer
                val vPlane = planes[2].buffer

                yPlane.get(yuvBytes, 0, width * height)

                val chromaRowStride = planes[1].rowStride
                val chromaRowPadding = chromaRowStride - width / 2

                var offset = width * height
                if (chromaRowPadding == 0) {
                    // When the row stride of the chroma channels equals their width, we can copy
                    // the entire channels in one go
                    uPlane.get(yuvBytes, offset, width * height / 4)
                    offset += width * height / 4
                    vPlane.get(yuvBytes, offset, width * height / 4)
                } else {
                    // When not equal, we need to copy the channels row by row
                    for (i in 0 until height / 2) {
                        uPlane.get(yuvBytes, offset, width / 2)
                        offset += width / 2
                        if (i < height / 2 - 1) {
                            uPlane.position(uPlane.position() + chromaRowPadding)
                        }
                    }
                    for (i in 0 until height / 2) {
                        vPlane.get(yuvBytes, offset, width / 2)
                        offset += width / 2
                        if (i < height / 2 - 1) {
                            vPlane.position(vPlane.position() + chromaRowPadding)
                        }
                    }
                }

                val yuvMat = Mat(height + height / 2, width, CvType.CV_8UC1)
                yuvMat.put(0, 0, yuvBytes)
                Imgproc.cvtColor(yuvMat, rgbaMat, Imgproc.COLOR_YUV2RGBA_I420, 4)
            }
        }

        return rgbaMat
    }

}