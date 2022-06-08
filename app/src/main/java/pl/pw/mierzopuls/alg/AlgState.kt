package pl.pw.mierzopuls.alg

sealed class AlgState {
    object NONE : AlgState()
    object Calibrate: AlgState()
    class Register(val calibration: Calibration): AlgState()

    override fun toString(): String {
        return when(this) {
            Calibrate -> "Calibrating"
            NONE -> "Start"
            is Register -> "Registering"
        }
    }
}
