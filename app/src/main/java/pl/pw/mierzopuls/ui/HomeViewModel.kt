package pl.pw.mierzopuls.ui

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.compose.runtime.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import pl.pw.mierzopuls.alg.AlgState
import pl.pw.mierzopuls.alg.Calibration
import pl.pw.mierzopuls.alg.ImageProcessing
import pl.pw.mierzopuls.model.Study
import pl.pw.mierzopuls.model.StudyRepository
import pl.pw.mierzopuls.util.getCameraProvider
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.coroutines.coroutineContext

class HomeViewModel(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    val navController: NavController
): ViewModel() {
    private val imageProcessing: ImageProcessing by inject(ImageProcessing::class.java)
    private val studyRepository: StudyRepository by inject(StudyRepository::class.java)
    private var lastTime by mutableStateOf(-1L)
    var timeStamps: List<Long> = listOf()
    var values: List<Double> = listOf()
    var algState: AlgState by mutableStateOf(AlgState.NONE)
    var camera: Camera? = null


    fun beginStudy() {
        lastTime = System.currentTimeMillis()
        algState = AlgState.Calibrate
    }

    fun beginRegistration() {
        lastTime = System.currentTimeMillis()
        algState = AlgState.Register(Calibration(values.average(),0.0,0.0))
        values = listOf()
        timeStamps = listOf()
    }

    fun showResult() {
        //TODO refactor
        imageAnalysisUseCase.clearAnalyzer()
        val parser = SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy")
        val formatter = SimpleDateFormat("dd.MM.yyyy_HH:mm")
        val output: String = formatter.format(Date.parse(Calendar.getInstance().time.toString()))
        val study = Study("study_${output}", values = values.map { it.toInt() }, timeStamps = timeStamps)
        algState = AlgState.NONE
        studyRepository.save(context, study)
    }

    fun dismissStudy() {
        lifecycleOwner.lifecycleScope.launch {
            context.getCameraProvider().let {
                it.unbindAll()
            }
        }
    }

    fun onHistory() {
        Toast.makeText(context, "History button clicked !", Toast.LENGTH_SHORT).show()
    }

    fun onStudy() {
        Toast.makeText(context, "Study button clicked !", Toast.LENGTH_SHORT).show()
    }

    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    @SuppressLint("UnsafeOptInUsageError")
    val imageAnalysisUseCase = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .apply {
            setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                val image: Image = imageProxy.image!!
                when(algState) {
                    is AlgState.NONE -> {
                        Log.w("ImageAnalyser","Alg state = NONE")
                    }
                    is AlgState.Calibrate -> {
                        values += imageProcessing.processImage(algState, image)

                        if (System.currentTimeMillis() - lastTime > 4000L) {  beginRegistration() }
                    }
                    is AlgState.Register -> {
                        values += imageProcessing.processImage(algState, image)
                        timeStamps += System.currentTimeMillis() - lastTime

                        if (System.currentTimeMillis() - lastTime > 13000L) { showResult() }
                    }
                }
                imageProxy.close()
            }
        }

    suspend fun prepareCamera() {
        val cameraProvider = context.getCameraProvider()
        try {
            // Must unbind the use-cases before rebinding them.
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, imageAnalysisUseCase
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
        algState = AlgState.Calibrate
    }
}