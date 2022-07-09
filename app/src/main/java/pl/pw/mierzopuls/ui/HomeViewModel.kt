package pl.pw.mierzopuls.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.Image
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import pl.pw.mierzopuls.alg.*
import pl.pw.mierzopuls.model.Study
import pl.pw.mierzopuls.model.StudyRepository
import pl.pw.mierzopuls.util.CameraLifecycle
import pl.pw.mierzopuls.util.getCameraProvider
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class HomeViewModel(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val navController: NavController,
    private val coroutineScope: CoroutineScope,
    private val launcher: ManagedActivityResultLauncher<String, Boolean>
): ViewModel() {
    private val cameraLifecycle: CameraLifecycle by inject(CameraLifecycle::class.java)
    private val imageProcessing: ImageProcessing by inject(ImageProcessing::class.java)
    private val studyRepository: StudyRepository by inject(StudyRepository::class.java)
    private var lastTime by mutableStateOf(-1L)
    var studies: List<Study> by mutableStateOf(studyRepository.readStudies(context))
    var timeStamps: List<Long> = listOf()
    var values: List<Double> = listOf()
    var algState: AlgState by mutableStateOf(AlgState.NONE)
    var studyOn: Boolean by mutableStateOf(false)


    fun beginStudy() {
        if (!checkPermissions()) return
        studyOn = true
        lastTime = System.currentTimeMillis()
        algState = AlgState.Calibrate
        coroutineScope.launch {
            prepareCamera()
        }
        cameraLifecycle.doOnStart()
    }

    private fun beginRegistration() {
        lastTime = System.currentTimeMillis()
        algState = AlgState.Register(Calibration(values.average(),0.0,0.0))
        values = listOf()
        timeStamps = listOf()
    }

    fun showResult() {
        //TODO refactor
        val parser = SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy")
        val formatter = SimpleDateFormat("dd.MM.yyyy_HH:mm")
        val output: String = formatter.format(Date.parse(Calendar.getInstance().time.toString()))
        val timeStart = timeStamps[0]
        val timeStop = timeStamps.last()

        val Fs = timeStamps.size/((timeStop - timeStart).toDouble()/1000)
        val filteredSignal = FIRFilter(values.toDoubleArray(), Fs)
        val peaks = peakFinder(timeStamps.map { it.toDouble() }.toDoubleArray(), filteredSignal)

        val pulse = calculatePulse(timeStamps.map { (it - timeStart).toDouble() }. filterIndexed { idx, _ ->  peaks.contains(idx) }.toDoubleArray())

        val study = Study(
            date = output,
            raw = values,
            times = timeStamps.map { (it - timeStart).toInt() },
            filtered = filteredSignal.toList(),
            peaks = peaks,
            pulse = pulse.toInt())

        algState = AlgState.Result(study)
        studyOn = false
        studyRepository.save(context, study)
        studies += study
        coroutineScope.launch {
            context.getCameraProvider().unbindAll()
            cameraLifecycle.doOnDestroy()
        }
    }

    fun dismissResult() {
        algState = AlgState.NONE
    }

    fun onHistory() {
        navController.navigate("history")
    }

    fun onStudy() {
        Toast.makeText(context, "Study button clicked !", Toast.LENGTH_SHORT).show()
        navController.navigate("debug")
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

                        if (System.currentTimeMillis() - lastTime > 3000L) {  beginRegistration() }
                    }
                    is AlgState.Register -> {
                        values += imageProcessing.processImage(algState, image)
                        timeStamps += System.currentTimeMillis() - lastTime

                        if (System.currentTimeMillis() - lastTime > 10000L) { showResult() }
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
                cameraLifecycle, cameraSelector, imageAnalysisUseCase
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

    private fun checkPermissions(): Boolean {
        return when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) -> { true }
            else -> {
                launcher.launch(Manifest.permission.CAMERA)
                false
            }
        }
    }
}