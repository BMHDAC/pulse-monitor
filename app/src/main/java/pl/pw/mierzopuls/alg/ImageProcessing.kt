package pl.pw.mierzopuls.alg

import android.util.Log
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat

class ImageProcessing {
    init {
        if (!OpenCVLoader.initDebug()) {
            Log.e("ImgProc", "Unable to load OpenCV! BE")
            throw InstantiationException("OpenCV not loaded correctly!")
        } else {
            Log.d("ImgProc","OpenCV library loaded correctly")
        }
    }

    fun statsPixelValue(mat: Mat): List<PixelStats> {
        var colors: Array<Array<Double>> = PixelColor.values()
            .map {
                PixelStats(it).let { stats ->
                    arrayOf(
                        stats.min.toDouble(),
                        stats.mean,
                        stats.max.toDouble(),
                    )
                }
            }.toTypedArray()
        for (i in 0..mat.rows()) {
            for (j in 0..mat.cols()) {
                for (color in PixelColor.values()) {
                    if (mat.get(i, j)[color.idx] < colors[color.idx][PixelStats.MIN]) {
                        colors[color.idx][PixelStats.MIN] = mat.get(i, j)[color.idx]
                    }
                    if (mat.get(i, j)[color.idx] > colors[color.idx][PixelStats.MAX]) {
                        colors[color.idx][PixelStats.MAX] = mat.get(i, j)[color.idx]
                    }
                    colors[color.idx][PixelStats.MEAN] += mat.get(i, j)[color.idx]
                }
            }
        }
        return colors.mapIndexed { idx, stats ->
            PixelStats(
                PixelColor.getPixelColorFromIndex(idx),
                stats[PixelStats.MIN].toInt(),
                stats[PixelStats.MEAN] / (mat.rows() * mat.cols()),
                stats[PixelStats.MAX].toInt()
            )
        }
    }

    fun meanValue(mat: Mat) {

    }

    fun maxValue(mat: Mat) {

    }

    fun minValue(mat: Mat) {

    }
}

data class PixelStats(val color: PixelColor,
                      val min: Int = 255,
                      val mean: Double = 0.0,
                      val max: Int = 0) {
    companion object {
        const val MIN = 0
        const val MEAN = 1
        const val MAX = 2
    }
}