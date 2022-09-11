package pl.pw.mierzopuls.alg

sealed class AlgState {
    object DEBUG : AlgState()
    object NONE : AlgState()
    class Calibrate(var isFingerInPlace: Boolean = false) : AlgState()
    class Register(val calibration: Calibration) : AlgState()
    object Finished : AlgState()
    class Result(val pulse: Int): AlgState()
}
