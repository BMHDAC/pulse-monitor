package pl.pw.mierzopuls.alg

import pl.pw.mierzopuls.model.Study

sealed class AlgState {
    object NONE : AlgState()
    object Calibrate: AlgState()
    class Register(val calibration: Calibration): AlgState()
    class Result(val study: Study): AlgState()

    override fun toString(): String {
        return when(this) {
            Calibrate -> "Calibrating"
            NONE -> "Start"
            is Register -> "Registering"
            is Result -> "puls: ${this.study.pulse}"
        }
    }
}
