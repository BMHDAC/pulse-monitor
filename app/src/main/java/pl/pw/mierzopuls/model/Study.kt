package pl.pw.mierzopuls.model

import com.google.gson.Gson
import java.util.*

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
    val date: String,
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

fun Study.toJson(): String = Gson().toJson(this)

fun String.toStudy(): Study = Gson().fromJson(this, Study::class.java)