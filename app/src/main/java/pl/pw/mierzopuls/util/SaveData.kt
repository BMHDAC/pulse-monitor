package pl.pw.mierzopuls.util

import android.content.Context
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import java.util.*

typealias Matrix = List<List<Double>>

fun saveMatrixData(context: Context, data: Matrix, tag: String) {
    context.applicationInfo.dataDir.let { dir ->
        File(dir,
            "${tag}_${UUID.randomUUID().toString().take(4)}.csv"
        ).let { file ->
            csvWriter().writeAll(data.drop(1), file.outputStream())
        }
    }
}