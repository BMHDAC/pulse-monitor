package pl.pw.mierzopuls.model

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

fun sendEvent(context: Context, study: Study) {
    FirebaseAnalytics.getInstance(context).logEvent(
        "event_study", Bundle().apply {
            putInt("pulse", study.pulse)
            putString("date", study.date)
        }
    )
}