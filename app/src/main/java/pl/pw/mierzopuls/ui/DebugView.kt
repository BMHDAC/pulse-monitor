package pl.pw.mierzopuls.ui

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.camera.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.data.Entry
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import pl.pw.mierzopuls.alg.AlgState
import pl.pw.mierzopuls.alg.ImageProcessing
import pl.pw.mierzopuls.ui.components.CameraPreview
import pl.pw.mierzopuls.ui.components.Checkbox
import pl.pw.mierzopuls.util.Permission
import pl.pw.mierzopuls.util.getCameraProvider
import java.util.concurrent.Executors

@androidx.camera.core.ExperimentalGetImage
@ExperimentalPermissionsApi
@Composable
fun DebugView(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
) {
    val context = LocalContext.current
    //TODO: move constants here
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
        var checkedState by remember { mutableStateOf(true) }
        var analysisEnabled: Boolean by remember { mutableStateOf(false) }
        var currentRadius: Double by remember { mutableStateOf(0.0) }
        val lifecycleOwner = LocalLifecycleOwner.current
        val coroutineScope = rememberCoroutineScope()
        val values = listOf<Int>()
        var previewUseCase = Preview.Builder().build() as UseCase
        val imageProcessing: ImageProcessing by remember { mutableStateOf(ImageProcessing()) }
        var lastTime: Long by remember { mutableStateOf(System.currentTimeMillis()) }
        val imageAnalysisUseCase = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                    if (analysisEnabled) {
                        val analysedMat = imageProcessing.analyseDebug(imageProxy.image!!)
                        analysedBitmap = imageProcessing.matToBitmap(analysedMat)
                        //currentRadius = currentRadius++
                        val fps = getFPS(lastTime, System.currentTimeMillis())
                        lastTime = System.currentTimeMillis()
                        Log.d("Analysis","FPS = $fps")
                        Log.d("Study", "value = ${imageProcessing.value}")
                        imageProcessing.values += imageProcessing.value
                        imageProcessing.limeStamps += lastTime
                    }
                imageProxy.close()
                }
            }
        LaunchedEffect(previewUseCase) {
            val cameraProvider = context.getCameraProvider()
            try {
                // Must unbind the use-cases before rebinding them.
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, previewUseCase, imageAnalysisUseCase
                )
                val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                    10F, 10F
                )
                val autoFocusPoint = factory.createPoint(1F, 1F)

                camera.cameraControl.enableTorch(true)
                camera.cameraControl.startFocusAndMetering(FocusMeteringAction.Builder(autoFocusPoint).disableAutoCancel().build())
            } catch (ex: Exception) {
                Log.e("CameraCapture", "Failed to bind camera use cases", ex)
            }
        }
        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.width(360.dp)
                .height(360.dp)
                .padding(16.dp)) {
                CameraPreview(
                    modifier = Modifier,
                    onUseCase = { previewUseCase = it }
                )
            }
            Row {
                Button(onClick = {
                    analysisEnabled = !analysisEnabled
                    if (imageProcessing.value != -100) {
//                        FileManager().save(context,
//                            AlgState.Study(
//                                "test2",
//                                imageProcessing.limeStamps.map { it.toDouble() },
//                                imageProcessing.values
//                            )
//                        )
                    }
                }) { Text("Start") }
                Checkbox(
                    title = "adadad",
                    checked = checkedState,
                    onCheckedChange = { checkedState = it }
                )
            }
            Row {
                BoxWithConstraints(modifier = Modifier.padding(16.dp)) {
                    Image(painter = BitmapPainter(analysedBitmap.asImageBitmap()), contentDescription ="" )
                }
            }
            Row {
                Text(text = "Current radius = $currentRadius")
            }
            Row {
//                LineChart(modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp),
//                    entries = listOf(
//                        Entry(1f,1f),
//                        Entry(2f,2f),
//                        Entry(3f,3f),
//                        Entry(4f,4f),
//                        Entry(5f,5f),
//                        ),
//                    xLabel = "time",
//                    yLabel = "data")
            }
        }
    }
}

fun getFPS(last: Long, now: Long): Long {
    return 1000L/(now - last)
}