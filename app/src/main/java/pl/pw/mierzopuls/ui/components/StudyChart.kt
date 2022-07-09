package pl.pw.mierzopuls.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import pl.pw.mierzopuls.R
import pl.pw.mierzopuls.model.Study


@Composable
fun StudyChart(
    modifier: Modifier = Modifier,
    study: Study,
    xLabel: String = "", //TODO: draw axis labels
    yLabel: String = ""
) {
    val line1 = study.raw.zip(study.times).map {
        Entry(it.second.toFloat(), it.first.toFloat())
    }
    val line1Label = stringResource(id = R.string.study_chart_line1)
    val line2 = study.filtered.zip(study.times).map {
        Entry(it.second.toFloat(), it.first.toFloat())
    }
    val line2Label = stringResource(id = R.string.study_chart_line2)
    val points = study.filtered.zip(study.times).filterIndexed { idx, _ ->
        study.peaks.contains(idx)
    }.map {
        Entry(it.second.toFloat(), it.first.toFloat())
    }
    val pointsLabel = stringResource(id = R.string.study_chart_points)

    AndroidView(
        modifier = modifier
            .height(250.dp)
            .fillMaxWidth(),
        factory = { context ->
            val chart = CombinedChart(context)

            chart.description.isEnabled = false
            chart.setBackgroundColor(Color.WHITE)
            chart.setDrawGridBackground(false)
            chart.setDrawBarShadow(false)
            chart.isHighlightFullBarEnabled = false
            chart.drawOrder = arrayOf(
                DrawOrder.LINE, DrawOrder.SCATTER
            )

            val lineData = LineData(
                LineDataSet(line1, line1Label).apply {
                    color = Color.rgb(100, 238, 170)
                    setDrawCircles(false)
                },
                LineDataSet(line2, line2Label).apply {
                    color = Color.rgb(200, 4, 0)
                    setDrawCircles(false)
                }
            )
            val pointsData = ScatterData(
                ScatterDataSet(points, pointsLabel).apply {
                    color = Color.rgb(200, 4, 0)
                    setScatterShape(ScatterChart.ScatterShape.SQUARE)
                }
            )
            val data = CombinedData()
            data.setData(lineData)
            data.setData(pointsData)

            chart.data = data

            val xAxis: XAxis = chart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM

            chart.invalidate()
            chart
        })
}