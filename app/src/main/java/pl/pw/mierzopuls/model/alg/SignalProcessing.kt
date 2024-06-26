package pl.pw.mierzopuls.model.alg

import com.github.psambit9791.jdsp.filter.Butterworth
import com.github.psambit9791.jdsp.signal.peaks.FindPeak
import com.github.psambit9791.jdsp.signal.peaks.Peak
import pl.pw.mierzopuls.model.Study
import pl.pw.mierzopuls.model.UserInfo
import pl.pw.mierzopuls.model.formatStudyDate
import java.util.*
import kotlin.math.roundToInt

fun processSignal(
    raw: List<Double>,
    times: List<Int>,
    userInfo: UserInfo
): Study {
    val filtered: DoubleArray = highpassFilter(
        rawSignal = raw.toDoubleArray(),
        times = times.map { it.toDouble() / 1000 }.toDoubleArray()
    )
    // drop first 50 samples due to filtering disruption
    val peaks: List<Int> = peakFinder(
        filtered.drop(50).toDoubleArray()
    )
    val pulse: Int = calculatePulse(
        times.drop(50),
        peaks
    ).roundToInt()

    return Study(
        date = Calendar.getInstance().formatStudyDate(),
        raw = raw,
        times = times.drop(50).map { it - times[0] },
        filtered = filtered.drop(50),
        peaks = peaks,
        pulse = pulse,
        userInfo = userInfo
    )
}

fun highpassFilter(
    rawSignal: DoubleArray, // px
    times: DoubleArray // s
): DoubleArray {
    val timeStart: Double = times.first()
    val timeEnd: Double = times.last()

    val fs: Double = times.size / (timeEnd - timeStart) // Hz
    val order = 200
    val cutOff = 1.0 // Hz

    return Butterworth(fs).highPassFilter(rawSignal, order, cutOff)
}

fun peakFinder(
    signal: DoubleArray
): List<Int> {
    val detectedPeaks: Peak = FindPeak(signal)
        .detectPeaks()
    val filtered: IntArray = detectedPeaks
        .filterByWidth(4.0, null)
    return filtered.toList()
}

fun calculatePulse(
    times: List<Int>,
    peaksIds: List<Int>
): Double {
    val peakTimestamps = times.filterIndexed { idx, _ ->
        peaksIds.contains(idx)
    }
    val quantil2 = peakTimestamps.mapIndexed { idx, time ->
        if (idx + 1 >= peakTimestamps.lastIndex) 0.0
        else (peakTimestamps[idx + 1] - time) / 1000.0
    }.median()

    return 60.0 / quantil2
}

private fun List<Double>.median() = this.sorted().let {
    if (it.size % 2 == 0)
        (it[it.size / 2] + it[(it.size - 1) / 2]) / 2
    else
        it[it.size / 2]
}

