package pl.pw.mierzopuls.util

import java.util.*

fun Calendar.formatStudyDate(): String {
    return "${this[Calendar.DAY_OF_MONTH]}/${this[Calendar.MONTH]}/${this[Calendar.YEAR]} ${this[Calendar.HOUR]}:${this[Calendar.MINUTE]}"
}