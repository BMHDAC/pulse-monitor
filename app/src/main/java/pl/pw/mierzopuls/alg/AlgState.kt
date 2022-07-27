package pl.pw.mierzopuls.alg

import pl.pw.mierzopuls.model.Study

sealed class AlgState {
    object NONE : AlgState()
    class Prepare(var count: Int) : AlgState()
    object Calibrate : AlgState()
    class Register(val calibration: Calibration) : AlgState()
    class Result(val study: Study) : AlgState()
    companion object {
        const val REGISTRATION_TIME = 13000L
    }
}
