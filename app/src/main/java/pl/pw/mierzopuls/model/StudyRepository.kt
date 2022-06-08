package pl.pw.mierzopuls.model

import android.content.Context
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import pl.pw.mierzopuls.alg.AlgState
import java.io.File

class StudyRepository {
    fun save(context: Context, study: Study) {
        val dir = context.applicationInfo.dataDir
        val file = File(dir, "${study.id}.csv")

        val rows = study.timeStamps.zip(study.values).map { listOf(it.first, it.second) }
        csvWriter().writeAll(rows, file)
    }
}