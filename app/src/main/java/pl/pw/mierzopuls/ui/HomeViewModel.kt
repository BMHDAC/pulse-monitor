package pl.pw.mierzopuls.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import org.koin.java.KoinJavaComponent.inject
import pl.pw.mierzopuls.alg.ImageProcessing

class HomeViewModel(
    private val context: Context) {
    private val imageProcessing: ImageProcessing by inject(ImageProcessing::class.java)

    fun onHistory() {
        Toast.makeText(context, "History button clicked !", Toast.LENGTH_SHORT).show()
    }

    fun onStudy() {
        Toast.makeText(context, "Study button clicked !", Toast.LENGTH_SHORT).show()
    }
}