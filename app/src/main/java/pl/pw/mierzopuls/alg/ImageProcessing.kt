package pl.pw.mierzopuls.alg

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.nio.ByteBuffer
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class ImageProcessing {
    var state = AlgState.START
    var calibration: Calibration? = null
    var currentRadius: Double? = 0.0

    private var calibrationStart: Long? = null
    private var redAcc: Double = 0.0
    private var probeCounter = 0

    companion object {
        private const val LOG_TAG = "ImgProc"
        val CALIBRATION_TIME = 3000L
        init {
            if (!OpenCVLoader.initDebug()) {
                Log.e(LOG_TAG, "Unable to load OpenCV! BE")
                throw InstantiationException("OpenCV not loaded correctly!")
            } else { Log.d(LOG_TAG,"OpenCV library loaded correctly") }
        }
    }

    fun analyse(image: Image): Mat {
        val mat = image.yuvToRgba()
        when (state) {
            AlgState.START -> {
                state = AlgState.CALIBRATION

                calibrationStart = System.currentTimeMillis()
                Log.d(LOG_TAG, "Calibration start: $calibrationStart")
            }
            AlgState.CALIBRATION -> {
                val meanPixelValue = Core.mean(mat)
                Log.d(LOG_TAG, """
                        red: ${meanPixelValue.`val`[0]}, 
                        green: ${meanPixelValue.`val`[1]},
                        blue: ${meanPixelValue.`val`[2]}
                    """.trimIndent())

                redAcc += meanPixelValue.`val`[0]
                probeCounter++

                if (System.currentTimeMillis() - calibrationStart!! > CALIBRATION_TIME) {
                    calibration = Calibration(
                        (redAcc/probeCounter), 0.0, 0.0
                    )
                    state = AlgState.ANALYZE
                    Log.d(LOG_TAG, "Calibration = $calibration")
                }
            }
            AlgState.ANALYZE -> {
                val threshold = calibration!!.getThreshold(10)
                Core.inRange(mat, threshold.first, threshold.second, mat)

                val centerOfBlob = getCenterOfBlob(mat)
                Log.d(LOG_TAG, "center of blob: x = ${centerOfBlob.first}, y = ${centerOfBlob.second}")

                currentRadius = calculateMeanRadius(mat, centerOfBlob)
                Log.d(LOG_TAG, "mean radius = $currentRadius")
            }
        }
        return mat
    }

    fun matToBitmap(mat: Mat): Bitmap {
        val bitmap = mat.let { it1 -> Bitmap.createBitmap(it1.cols(), mat.rows(), Bitmap.Config.ARGB_8888) }
        Utils.matToBitmap(mat, bitmap)
        return bitmap
    }

    fun getCenterOfBlob(mat: Mat): Pair<Int, Int> {
        var centerOfBlob = 0.0 to 0.0
        for (i in 0 until mat.rows()) {
            for (j in 0 until mat.cols()) {
                val value = if (mat.get(i,j)[0] == 0.0) 0 else 1
                centerOfBlob = centerOfBlob.first + i * value to
                        centerOfBlob.second + j * value
            }
        }
        return (centerOfBlob.first / (mat.rows()*255)).toInt() to
                (centerOfBlob.second / (mat.cols()*255)).toInt()
    }

    enum class Direction(val xOffset: Int, val yOffset: Int) {
        `0`(1,0),
        `45`(1,1),
        `90`(0,1),
        `135`(-1,1),
        `180`(-1,0),
        `225`(-1,-1),
        `270`(0,-1),
        `315`(1,-1),
    }

    private fun calculateSingleRadius(mat: Mat, center: Pair<Int, Int>, direction: Direction): Double? {
        var stopPoint = center
        while (true) {
            stopPoint = stopPoint.first + direction.xOffset to stopPoint.second + direction.yOffset
            if (stopPoint.first >= mat.rows() || stopPoint.first < 0 ||
                stopPoint.second >= mat.cols() || stopPoint.second < 0) {
                return null
            }
            if (mat.get(stopPoint.first, stopPoint.second)[0] == 0.0) {
                return sqrt(
                    abs(
                        (center.first - stopPoint.first).toDouble()
                    ).pow(2) + abs(
                        (center.second - stopPoint.second).toDouble()
                    ).pow(2)
                )
            }
        }
    }

    fun calculateMeanRadius(mat: Mat, center: Pair<Int, Int>): Double? {
        val radiuses = Direction.values().map { calculateSingleRadius(mat,center,it) }

        var acc = 0.0
        var counter = 0
        for (radius in radiuses) {
            if (radius != null) {
                acc += radius
                counter++
            }
        }

        return if (counter != 0) acc / counter else null
    }

    private fun Image.yuvToRgba(): Mat {
        val rgbaMat = Mat()

        if (format == ImageFormat.YUV_420_888
            && planes.size == 3) {

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