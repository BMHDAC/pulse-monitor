package pl.pw.mierzopuls.model

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class StudyRepository {
    fun save(
        context: Context,
        study: Study,
        target: SaveTarget = SaveTarget.APP
    ) {
        when(target) {
            SaveTarget.APP -> {
                File(
                    context.applicationInfo.dataDir,
                    "${study.id}.json"
                ).writeText(
                    study.toJson()
                )
            }
            is SaveTarget.EXPORT -> {
                target.uri
                    .toFile()
                    .writeText(study.toJson())
            }
        }
    }

    suspend fun readStudies(
        context: Context
    ): List<Study>? {
        return withContext(Dispatchers.IO) {
            context.applicationInfo.dataDir.let { dir ->
                File(dir).listFiles { _, s ->
                    s.takeLast(5) == ".json"
                }?.map { file ->
                    file.readText().toStudy()
                }
            }
        }
    }

    fun readStudy(context: Context, id: String): Study {
        return context.applicationInfo.dataDir.let { dir ->
            File(dir, "${id}.json").readText().toStudy()
        }
    }
}

sealed class SaveTarget {
    object APP: SaveTarget()
    class EXPORT(val uri: Uri): SaveTarget()
}