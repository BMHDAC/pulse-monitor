package pl.pw.mierzopuls.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.camera.core.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import pl.pw.mierzopuls.alg.AlgState
import pl.pw.mierzopuls.alg.AlgState.Calibrate.Companion.CALIBRATION_MS
import pl.pw.mierzopuls.alg.AlgState.Register.Companion.REGISTRATION_TIME
import pl.pw.mierzopuls.alg.Calibration
import pl.pw.mierzopuls.alg.ImageProcessing
import pl.pw.mierzopuls.alg.processSignal
import pl.pw.mierzopuls.model.*
import pl.pw.mierzopuls.util.CameraLifecycle
import pl.pw.mierzopuls.util.getCameraProvider

class HomeViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val cameraLifecycle: CameraLifecycle by inject(CameraLifecycle::class.java)
    private val imageProcessing: ImageProcessing by inject(ImageProcessing::class.java)
    private val studyRepository: StudyRepository by inject(StudyRepository::class.java)
    private val appSetting: AppSetting by inject(AppSetting::class.java)
    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var timeStamps: MutableList<Long> = mutableListOf()
    private var values: MutableList<Double> = mutableListOf()

    var studies: List<Study> by mutableStateOf(listOf())
    var algState: AlgState by mutableStateOf(AlgState.NONE)
    var openInstruction by mutableStateOf(appSetting.showInstructionOnStart)

    fun initRepository() {
        studies = studyRepository.readStudies(getApplication()).sortByDate()
    }

    fun beginStudy(launcher: ManagedActivityResultLauncher<String, Boolean>, coroutineScope: CoroutineScope) {
        if (!checkPermissions(launcher)) return
        clearData()
        coroutineScope.launch {
            prepareCamera(getApplication())
        }
        beginCalibration(coroutineScope)
        cameraLifecycle.doOnStart()
    }

    fun beginCalibration(coroutineScope: CoroutineScope) {
        algState = AlgState.Calibrate()
        object : CountDownTimer(CALIBRATION_MS, CALIBRATION_MS.div(10)) {
            override fun onTick(millisUntilFinished: Long) {
                if (algState is AlgState.NONE) return this.cancel()
                if ((algState as AlgState.Calibrate).isCorrupted) {
                    this.cancel()
                    clearData()
                    beginCalibration(coroutineScope)
                }
            }
            override fun onFinish() = beginRegistration(Calibration.getCalibration(values), coroutineScope)
        }.start()
    }

    fun dismissStudy(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            (getApplication() as Context).getCameraProvider().unbindAll()
            cameraLifecycle.doOnDestroy()
            algState = AlgState.NONE
        }
    }

    private fun beginRegistration(calibration: Calibration, coroutineScope: CoroutineScope) {
        algState = AlgState.Register(calibration)
        clearData()
        object : CountDownTimer(REGISTRATION_TIME, REGISTRATION_TIME) {
            override fun onTick(millisUntilFinished: Long) = Unit
            override fun onFinish() = showResult(coroutineScope)
        }.start()
    }

    private fun showResult(coroutineScope: CoroutineScope) {
        val context: Context = getApplication()
        coroutineScope.launch {
            cameraLifecycle.doOnDestroy()
            context.getCameraProvider().unbindAll()
        }
        processSignal(values, timeStamps.map { (it - timeStamps[0]).toInt() }).let { study ->
            algState = AlgState.Result(study)
            studyRepository.save(context, study)
            studies = study + studies
            sendEvent(context, study)
        }
    }

    private fun clearData() {
        values.clear()
        timeStamps.clear()
    }

    @SuppressLint("UnsafeOptInUsageError")
    private val imageAnalysisUseCase = imageProcessing.imageAnalysisUseCase { image ->
        when (algState) {
            is AlgState.NONE,
            is AlgState.DEBUG,
            is AlgState.Result-> {
                Log.w(ImageProcessing.LOG_TAG, "Alg state = ${algState.javaClass}")
            }
            is AlgState.Calibrate -> {
                val mean = imageProcessing.getRGBStats(image)
                if (mean[1] > 100 || mean[2] > 100 || mean[0] < 220) {
                    (algState as AlgState.Calibrate).isCorrupted = true
                } else values += mean[0]
            }
            is AlgState.Register -> {
                values += imageProcessing.processImage(algState, image)
                timeStamps += System.currentTimeMillis()
            }
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