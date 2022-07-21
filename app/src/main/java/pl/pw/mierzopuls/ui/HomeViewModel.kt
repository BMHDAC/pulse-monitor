package pl.pw.mierzopuls.ui

import android.Manifest
import android.annotation.SuppressLint
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
import pl.pw.mierzopuls.model.Study
import pl.pw.mierzopuls.model.StudyRepository
import pl.pw.mierzopuls.model.sendEvent
import pl.pw.mierzopuls.util.CameraLifecycle
import pl.pw.mierzopuls.util.getCameraProvider
import java.util.concurrent.Executors

class HomeViewModel(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val navController: NavController,
    private val coroutineScope: CoroutineScope,
    private val launcher: ManagedActivityResultLauncher<String, Boolean>
) : ViewModel() {
    private val cameraLifecycle: CameraLifecycle by inject(CameraLifecycle::class.java)
    private val imageProcessing: ImageProcessing by inject(ImageProcessing::class.java)
    private val studyRepository: StudyRepository by inject(StudyRepository::class.java)
    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var lastTime by mutableStateOf(-1L)
    private var timeStamps: List<Long> = listOf()
    private var values: List<Double> = listOf()

    var studies: List<Study> by mutableStateOf(studyRepository.readStudies(context)) // TODO: fetch for studies async
    var algState: AlgState by mutableStateOf(AlgState.NONE)
    var openInstruction by mutableStateOf(false)

    fun beginStudy() {
        if (!checkPermissions()) return
        lastTime = System.currentTimeMillis()
        values = listOf()
        timeStamps = listOf()
        coroutineScope.launch {
            prepareCamera()
        }
        algState = AlgState.CountDown(3)
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                algState = AlgState.CountDown((millisUntilFinished / 1000 + 1).toInt())
            }

            override fun onFinish() = beginRegistration()

        }.start()
        cameraLifecycle.doOnStart()
    }

    fun dismissResult() {
        algState = AlgState.NONE
    }

    fun onHistory() {
        navController.navigate("history")
    }

    private fun beginRegistration() {
        lastTime = System.currentTimeMillis()
        algState = AlgState.Register
        values = listOf()
        timeStamps = listOf()
    }

    private fun showResult() {
        coroutineScope.launch {
            processSignal(values, timeStamps.map { (it - timeStamps[0]).toInt() }).let { study ->
                algState = AlgState.Result(study)
                studyRepository.save(context, study)
                studies += study
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

                        if (System.currentTimeMillis() - lastTime > AlgState.REGISTRATION_TIME) {
                            showResult()
                        }
                    }
                }
                imageProxy.close()
            }
        }

    private suspend fun prepareCamera() {
        val cameraProvider = context.getCameraProvider()
        try {
            // Must unbind the use-cases before rebinding them.
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                cameraLifecycle, cameraSelector, imageAnalysisUseCase
            )
            val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                10F, 10F
            )
            val autoFocusPoint = factory.createPoint(1F, 1F)

            camera.cameraControl.enableTorch(true)
            camera.cameraControl.startFocusAndMetering(
                FocusMeteringAction.Builder(autoFocusPoint).disableAutoCancel().build()
            )
            Log.d("prepareCamera", "finished preparation")
        } catch (ex: Exception) {
            Log.e("CameraCapture", "Failed to bind camera use cases", ex)
        }
    }

    private fun checkPermissions(): Boolean {
        return when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context,
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