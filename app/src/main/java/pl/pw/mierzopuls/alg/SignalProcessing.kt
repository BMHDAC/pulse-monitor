package pl.pw.mierzopuls.alg

import com.github.psambit9791.jdsp.filter.FIRWin1
import com.github.psambit9791.jdsp.signal.peaks.FindPeak
import pl.pw.mierzopuls.model.Study
import pl.pw.mierzopuls.util.formatStudyDate
import java.util.*

fun processSignal(raw: List<Double>, times: List<Int>): Study {
    val meanRaw = raw.average()
    val baseLineRemoval = raw.map { it - meanRaw }
    val filteredSignal = firFilter(baseLineRemoval.toDoubleArray(), times.map { it.toDouble() }.toDoubleArray())
    val peaks = peakFinder(filteredSignal)
    val pulse = calculatePulse(times, peaks)

    return Study(
        date = Calendar.getInstance().formatStudyDate(),
        raw = baseLineRemoval,
        times = times,
        filtered = filteredSignal.toList(),
        peaks = peaks,
        pulse = pulse.toInt()
    )
}

fun firFilter(rawSignal: DoubleArray, times: DoubleArray): DoubleArray {
    val timeStart = times[0]
    val timeStop = times.last()

    val fs = times.size / ((timeStop - timeStart) / 1000)
    val width = 4.0
    val taps = 5
    val cutoff: DoubleArray = doubleArrayOf(0.5)

    val fw = FIRWin1(taps, width, fs)
    val outCoffs = fw.computeCoefficients(cutoff, FIRWin1.FIRfilterType.LOWPASS, true)

    return fw.firfilter(outCoffs, rawSignal)
}

fun peakFinder(signal: DoubleArray): List<Int> {
    val fp = FindPeak(signal)
    val detectedPeaks = fp.detectPeaks()
    val outFilteredDistance1 = detectedPeaks.filterByPeakDistance(10) //To filter peaks by distance

    return outFilteredDistance1.toList()
}

fun calculatePulse(times: List<Int>, peaksIds: List<Int>): Double {
    return times.filterIndexed { idx, _ ->
        peaksIds.contains(idx)
    }.let {
        (it.last() - it[0]) / 60000.0
    }.let { duration ->
        (peaksIds.size - 1) / duration
    }
}