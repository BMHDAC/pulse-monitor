package pl.pw.mierzopuls.alg

import pl.pw.mierzopuls.model.Study

sealed class AlgState {
    object DEBUG : AlgState()
    object NONE : AlgState()
    class Calibrate(var isFingerInPlace: Boolean = false) : AlgState() {
        companion object {
            const val CALIBRATION_MS = 3000L
        }
    }
    class Register(val calibration: Calibration) : AlgState() {
        companion object {
            const val REGISTRATION_TIME_MS = 15000L
        }
    }
    class Result(val study: Study) : AlgState()
}
