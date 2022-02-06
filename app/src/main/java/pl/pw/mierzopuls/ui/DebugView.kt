package pl.pw.mierzopuls.ui

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.opencv.android.Utils
import pl.pw.mierzopuls.alg.ImageProcessing
import pl.pw.mierzopuls.ui.components.CameraPreview
import pl.pw.mierzopuls.util.Permission
import pl.pw.mierzopuls.util.getCameraProvider
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@androidx.camera.core.ExperimentalGetImage
@ExperimentalPermissionsApi
@Composable
fun DebugView(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
) {
    val context = LocalContext.current
    Permission(
        permission = Manifest.permission.CAMERA,
        rationale = "You said you wanted a picture, so I'm going to have to ask for permission.",
        permissionNotAvailableContent = {
            Column(modifier) {
                Text("O noes! No Camera!")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    })
                }) { Text("Open Settings") }
            }
        }
    ) {
        var analysedBitmap: Bitmap by remember { mutableStateOf(
            Bitmap.createBitmap(640, 640, Bitmap.Config.ARGB_8888)
        ) }
        val lifecycleOwner = LocalLifecycleOwner.current
        val coroutineScope = rememberCoroutineScope()
        var previewUseCase = Preview.Builder().build() as UseCase
        val imageAnalysisUseCase = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        val imageProcessing = ImageProcessing()
        imageAnalysisUseCase.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
            analysedBitmap = imageProcessing.analyse(imageProxy.image!!)
            imageProxy.close()
        }
        Column(modifier = Modifier.fillMaxSize()) {
            Row {
                BoxWithConstraints(modifier = Modifier.padding(16.dp)) {
                    CameraPreview(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(this.maxWidth),
                        onUseCase = {
                            previewUseCase = it
                        }
                    )
                }

                LaunchedEffect(previewUseCase) {
                    val cameraProvider = context.getCameraProvider()
                    try {
                        // Must unbind the use-cases before rebinding them.
                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, previewUseCase, imageAnalysisUseCase
                        )
                        camera.cameraControl.enableTorch(true)
                    } catch (ex: Exception) {
                        Log.e("CameraCapture", "Failed to bind camera use cases", ex)
                    }
                }
            }
            Row {
                BoxWithConstraints(modifier = Modifier.padding(16.dp)) {
                    Image(painter = BitmapPainter(analysedBitmap.asImageBitmap()), contentDescription ="" )
                }
            }
        }
    }
}