package pl.pw.mierzopuls.model

import android.content.Context
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.gson.Gson
import pl.pw.mierzopuls.alg.AlgState
import java.io.File

class StudyRepository {
    fun save(context: Context, study: Study) {
        context.applicationInfo.dataDir.let { dir ->
            File(dir, "${study.id}.json").let { file ->
                file.writeText(
                    study.toJson()
                )
            }
        }
    }

    fun readStudies(context: Context): List<Study> {
        return context.applicationInfo.dataDir.let { dir ->
            File(dir).listFiles().map { file ->
                file.readText().toStudy()
            }
        }
    }

    fun readStudy(context: Context, id: String): Study {
        return context.applicationInfo.dataDir.let { dir ->
            File(dir, "${id}.json").let { file ->
                file.readText().toStudy()
            }
        }
    }
}