package pl.pw.mierzopuls.model

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics

const val STUDY_EVENT = "event_study"

fun sendEvent(context: Context, study: Study) {
    FirebaseAnalytics.getInstance(context).logEvent(
        STUDY_EVENT, Bundle().apply {
            putInt("pulse", study.pulse)
            putString("date", study.date)
            putInt("fps", study.fps())
        }
    )
    Log.d(STUDY_EVENT, """
        params:
        date = ${study.date}
        pulse = ${study.pulse}
        fps = ${study.fps()}
    """.trimIndent())
}