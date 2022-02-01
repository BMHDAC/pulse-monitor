package pl.pw.mierzopuls.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import org.koin.java.KoinJavaComponent.inject
import pl.pw.mierzopuls.alg.ImageProcessing
import pl.pw.mierzopuls.model.Study

class HomeViewModel(
    private val context: Context): ViewModel() {
    private val imageProcessing: ImageProcessing by inject(ImageProcessing::class.java)
    private val _studies = mutableStateOf(
        listOf(Study("id1"), Study("id2"), Study("id3"))
    )
    val studies: State<List<Study>>
        get() = _studies

    fun onHistory() {
        Toast.makeText(context, "History button clicked !", Toast.LENGTH_SHORT).show()
    }

    fun onStudy() {
        Toast.makeText(context, "Study button clicked !", Toast.LENGTH_SHORT).show()
    }
}