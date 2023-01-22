package pl.pw.mierzopuls.model.alg

sealed class AlgState {
    object NONE : AlgState()
    class Calibration(var isFingerInPlace: Boolean = false) : AlgState()
    object Register : AlgState()
    object Finished : AlgState()
    class Result(val pulse: Int): AlgState()
}
