package pl.pw.mierzopuls.alg

import pl.pw.mierzopuls.model.Study

sealed class AlgState {
    object NONE : AlgState()
    class CountDown(var count: Int) : AlgState()
    object Register : AlgState()
    class Result(val study: Study) : AlgState()
    companion object {
        const val REGISTRATION_TIME = 13000L
    }
}
