package pl.pw.mierzopuls.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    entries: List<Entry>,
    xLabel: String,
    yLabel: String
) {
    AndroidView(modifier = modifier,
        factory = { context ->
        val lineChart = com.github.mikephil.charting.charts.LineChart(context)

        val lineDataSet = LineDataSet(entries, yLabel)
        val lineData = LineData(lineDataSet)

        lineChart.data = lineData
        lineChart.invalidate()

        lineChart
    })
}