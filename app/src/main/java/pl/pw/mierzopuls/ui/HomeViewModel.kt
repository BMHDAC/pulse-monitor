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
    private val context: Context,
    private val onPermissionHandler: (String) -> Unit) {
    private val imageProcessing: ImageProcessing by inject(ImageProcessing::class.java)

    fun onStart() : Bitmap? {
        if (hasPermissions(context, arrayOf(Manifest.permission.CAMERA))) {
            //val camera = Camera(context)
            //val frame = camera.grabFrame()
            //val bitmap = frame.toBitmap(Camera.bitmapConverter)
            return null
        } else {
            onPermissionHandler(Manifest.permission.CAMERA)
        }
        return null
    }

    fun onHistory() {
        Toast.makeText(context, "History button clicked !", Toast.LENGTH_SHORT).show()
    }

    fun onStudy() {
        Toast.makeText(context, "Study button clicked !", Toast.LENGTH_SHORT).show()
    }

    fun getCameraPreview() : Bitmap? {
//        val camera = Camera()
//        val bitmap = camera.grabFrame().toBitmap(Camera.bitmapConverter)
        return null
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}