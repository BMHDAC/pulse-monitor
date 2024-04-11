package pl.pw.mierzopuls.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import pl.pw.mierzopuls.R
import pl.pw.mierzopuls.model.Study


@Composable
fun StudyChart(
    modifier: Modifier = Modifier,
    study: Study,
) {
    val line1 = study.filtered.zip(study.times).map {
        Entry(it.second.toFloat(), it.first.toFloat())
    }.drop(50)
    val line1Label = stringResource(id = R.string.study_chart_line1)
    val points = study.filtered.zip(study.times).filterIndexed { idx, _ ->
        study.peaks.contains(idx) && idx > 50
    }.map {
        Entry(it.second.toFloat(), it.first.toFloat())
    }
    val pointsLabel = stringResource(id = R.string.study_chart_points)

    Text(
        text = study.userInfo.name,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.size(48.dp)
    )

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