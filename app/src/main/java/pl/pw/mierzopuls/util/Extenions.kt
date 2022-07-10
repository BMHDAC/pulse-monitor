package pl.pw.mierzopuls.util

import com.google.gson.Gson
import pl.pw.mierzopuls.model.Study
import java.util.*

fun Calendar.formatStudyDate(): String {
    val minutes = this[Calendar.MINUTE].let { if (it < 9) "0$it" else "$it" }
    return "${this[Calendar.DAY_OF_MONTH]}/${this[Calendar.MONTH]}/${this[Calendar.YEAR]} ${this[Calendar.HOUR_OF_DAY]}:${minutes}"
}