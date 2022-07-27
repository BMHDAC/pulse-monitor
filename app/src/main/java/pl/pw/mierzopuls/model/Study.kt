package pl.pw.mierzopuls.model

import com.google.gson.Gson
import java.util.*

typealias StudyDate = String
/**
 * @property id unique id
 * @property date unique id
 * @property pulse detected pulse value
 * @property times timestamps
 * @property raw raw data
 * @property filtered processed and filtered data
 * @property peaks indexes of detected peaks
 * @constructor creates an empty group.
 */
data class Study(
    val id: String = UUID.randomUUID().toString(),
    val date: StudyDate,
    val pulse: Int,
    val times: List<Int> = listOf(),
    val raw: List<Double> = listOf(),
    val filtered: List<Double> = listOf(),
    val peaks: List<Int> = listOf()
)

fun Study.fps(): Int {
    val frames = this.times.size.toDouble()
    val periodMs = (this.times.last() - this.times.first()).toDouble()
    return (1000.0 * frames / periodMs).toInt()
}

operator fun Study.plus(list: List<Study>) = listOf(this, *list.toTypedArray())

fun List<Study>.sortByDate(): List<Study> {
    return this.map {
        it to it.date.toMs()
    }.sortedBy {
        it.second
    }.map {
        it.first
    }.reversed()
}

fun Study.toJson(): String = Gson().toJson(this)

fun String.toStudy(): Study = Gson().fromJson(this, Study::class.java)

fun StudyDate.toDisplay(): String = this.replace("_", "   ")

fun StudyDate.toMs(): Long {
    return this.slice(6..9).toLong() * 31556952000L + //year
            this.slice(3..4).toLong() * 2629800000L + //month
            this.slice(0..1).toLong() * 86400000L + //day
            this.slice(11..12).toLong() * 3600000L + //hour
            this.takeLast(2).toLong() * 60000L // min
}

fun Calendar.formatStudyDate(): StudyDate {
    val minutes = this[Calendar.MINUTE].let { if (it < 9) "0$it" else "$it" }
    val hours = this[Calendar.HOUR_OF_DAY].let { if (it < 9) "0$it" else "$it" }
    val month = this[Calendar.MONTH].let { if (it < 9) "0$it" else "$it" }
    val day = this[Calendar.MONTH].let { if (it < 9) "0$it" else "$it" }
    return "$day/$month/${this[Calendar.YEAR]} $hours:$minutes"
}