package pl.pw.mierzopuls.alg

import org.opencv.core.Scalar

data class Calibration(
    val redThreshold: Double,
    val greenThreshold: Double,
    val blueThreshold: Double
) {
    fun getThreshold(offset: Int): Pair<Scalar, Scalar> {
        return Scalar(
            redThreshold - offset,
            0.0,
            0.0,
            0.0
        ) to Scalar(
            redThreshold + offset,
            255.0,
            255.0,
            255.0
        )
    }
    companion object {
        const val CALIBRATION_MS = 4000L

        fun getCalibration(values: List<Double>): Calibration {
            return Calibration(
                values.average(),
                0.0,
                0.0
            )
        }
    }
}
