package pl.pw.mierzopuls.util

import com.github.mikephil.charting.data.Entry
import pl.pw.mierzopuls.model.Study

object SampleData {
    val studies = listOf(
        Study(date = "01.03.2022_12:13", pulse = 72),
        Study(date = "09.04.2022_03:24", pulse = 88),
        Study(date = "19.05.2022_11:39", pulse = 82),
        Study(date = "22.07.2022_13:51", pulse = 65),
        Study(date = "25.07.2022_18:12", pulse = 87),
        Study(date = "25.07.2022_18:32", pulse = 96)
    )
    val exampleLine = listOf(
        Entry(0f,0f),
        Entry(1f,1f),
        Entry(2f,2f),
        Entry(3f,3f),
        Entry(4f,4f),
        Entry(5f,5f),
    )
    val exampleLine2 = listOf(
        Entry(0f,6f),
        Entry(1f,5f),
        Entry(2f,4f),
        Entry(3f,3f),
        Entry(4f,2f),
        Entry(5f,1f),
    )
    val examplePoints = listOf(
        Entry(2f,1f),
        Entry(3f,2f),
        Entry(4f,5f)
    )
}