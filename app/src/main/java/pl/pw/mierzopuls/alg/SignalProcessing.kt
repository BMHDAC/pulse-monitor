package pl.pw.mierzopuls.alg

import com.github.psambit9791.jdsp.filter.FIRWin1
import com.github.psambit9791.jdsp.signal.peaks.FindPeak

fun FIRFilter(rawSignal: DoubleArray, fs: Double): DoubleArray {
    val max = rawSignal.maxByOrNull { it }
    val min = rawSignal.minByOrNull { it }

    val values_baseline = rawSignal.map { it - min!! }

    val width = 4.0
    val samplingRate = fs
    val taps = 5
    val cutoff: DoubleArray = doubleArrayOf(0.5)

    val signal = values_baseline.map { it.toDouble() }.toDoubleArray()

    val fw = FIRWin1(taps, width, samplingRate)
    val outCoeffs = fw.computeCoefficients(cutoff, FIRWin1.FIRfilterType.LOWPASS, true);
    val filteredX = fw.firfilter(outCoeffs, signal)

    return filteredX
}

fun peakFinder(time: DoubleArray, signal: DoubleArray): List<Int> {
    val fp = FindPeak(signal)
    val detectedPeaks = fp.detectPeaks()
    val peaks = detectedPeaks.peaks

    val outFilteredDistance1 = detectedPeaks.filterByPeakDistance(10); //To filter peaks by distance

    //val peakTimes = time.filterIndexed {  idx, _ -> outFilteredDistance1.contains(idx) }

    return outFilteredDistance1.toList()
}

fun calculatePulse(peaks: DoubleArray): Double {
    val duration = (peaks.last() - peaks[0])/60000.0

    return (peaks.size - 1)/duration
}