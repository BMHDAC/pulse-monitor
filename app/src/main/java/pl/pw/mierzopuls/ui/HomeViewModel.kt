package pl.pw.mierzopuls.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.media.Image
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.camera.core.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import pl.pw.mierzopuls.alg.AlgState
import pl.pw.mierzopuls.alg.Calibration
import pl.pw.mierzopuls.alg.ImageProcessing
import pl.pw.mierzopuls.alg.processSignal
import pl.pw.mierzopuls.model.*
import pl.pw.mierzopuls.util.CameraLifecycle
import pl.pw.mierzopuls.util.getCameraProvider
import java.util.concurrent.Executors

class HomeViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val cameraLifecycle: CameraLifecycle by inject(CameraLifecycle::class.java)
    private val imageProcessing: ImageProcessing by inject(ImageProcessing::class.java)
    private val studyRepository: StudyRepository by inject(StudyRepository::class.java)
    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var lastTime by mutableStateOf(-1L)
    private var timeStamps: List<Long> = listOf()
    private var values: List<Double> = listOf()

    var studies: List<Study> by mutableStateOf(studyRepository.readStudies(application).sortByDate()) // TODO: fetch for studies async
    var algState: AlgState by mutableStateOf(AlgState.NONE)
    var openInstruction by mutableStateOf(false)

    fun beginStudy(launcher: ManagedActivityResultLauncher<String, Boolean>, coroutineScope: CoroutineScope) {
        if (!checkPermissions(launcher)) return
        lastTime = System.currentTimeMillis()
        values = listOf()
        timeStamps = listOf()
        coroutineScope.launch {
            prepareCamera(getApplication())
        }
        algState = AlgState.CountDown(3)
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                algState = AlgState.CountDown((millisUntilFinished / 1000 + 1).toInt())
            }
            override fun onFinish() = beginRegistration(coroutineScope)
        }.start()
        cameraLifecycle.doOnStart()
    }

    fun dismissResult() {
        algState = AlgState.NONE
    }

//    fun onHistory() {
//        navController.navigate("history")
//    }

    private fun beginRegistration(coroutineScope: CoroutineScope) {
        lastTime = System.currentTimeMillis()
        algState = AlgState.Register
        values = listOf()
        timeStamps = listOf()
        object : CountDownTimer(AlgState.REGISTRATION_TIME, AlgState.REGISTRATION_TIME) {
            override fun onTick(millisUntilFinished: Long) = Unit

            override fun onFinish() = showResult(coroutineScope)
        }.start()
    }

    private fun showResult(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            val context: Context = getApplication()
            processSignal(values, timeStamps.map { (it - timeStamps[0]).toInt() }).let { study ->
                algState = AlgState.Result(study)
                studyRepository.save(context, study)
                studies = study + studies
                sendEvent(context, study)
            }
            context.getCameraProvider().unbindAll()
            cameraLifecycle.doOnDestroy()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private val imageAnalysisUseCase = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .apply {
            setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                val image: Image = imageProxy.image!!
                when (algState) {
                    is AlgState.CountDown -> {}
                    is AlgState.NONE,
                    is AlgState.Result-> {
                        Log.w("ImageAnalyser", "Alg state = ${algState.javaClass}")
                    }
                    is AlgState.Register -> {
                        values += imageProcessing.processImage(algState, image)
                        timeStamps += System.currentTimeMillis() - lastTime
                    }
                }
                imageProxy.close()
            }
        }

    private suspend fun prepareCamera(context: Context) {
        val cameraProvider = context.getCameraProvider()
        try {
            // Must unbind the use-cases before rebinding them.
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                cameraLifecycle, cameraSelector, imageAnalysisUseCase
            )
            camera.cameraControl.enableTorch(true)
            Log.d("CameraPrep", "torch enabled")

            val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                10F, 10F
            )
            val autoFocusPoint = factory.createPoint(1F, 1F)
            camera.cameraControl.startFocusAndMetering(
                FocusMeteringAction.Builder(autoFocusPoint).disableAutoCancel().build()
            )
            Log.d("CameraPrep", "autofocus disabled")
        } catch (ex: Exception) {
            Log.e("CameraCapture", "Failed to bind camera use cases", ex)
        }
    }

    private fun checkPermissions(launcher: ManagedActivityResultLauncher<String, Boolean>): Boolean {
        return when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.CAMERA
            ) -> {
                true
            }
            else -> {
                launcher.launch(Manifest.permission.CAMERA)
                false
            }
        }
    }
}