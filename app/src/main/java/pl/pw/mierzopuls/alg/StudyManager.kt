package pl.pw.mierzopuls.alg

import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.MeteringPointFactory
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import pl.pw.mierzopuls.util.CameraLifecycle
import pl.pw.mierzopuls.util.getCameraProvider


class StudyManager(
    private val cameraLifecycle: CameraLifecycle,
    private val imageProcessing: ImageProcessing,
) {
    companion object {
        const val CALIBRATION_TIME = 2000L
        const val REGISTRATION_TIME = 27000L
    }
    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private val timeStamps = mutableListOf<Long>()
    private val values = mutableListOf<Double>()
    private var startTime = 0L
    private var onResult: (List<Long>, List<Double>) -> Unit = { _, _ -> }
    private var vibrator: Vibrator? = null

    var algState: AlgState by mutableStateOf(AlgState.NONE)
    var progress: Float by mutableStateOf(0.0f)

    @SuppressLint("UnsafeOptInUsageError")
    val imageAnalysisUseCase = imageProcessing.imageAnalysisUseCase { image ->
        when (val state = algState) {
            is AlgState.NONE,
            is AlgState.Finished-> {
                Log.w(ImageProcessing.LOG_TAG, "Alg state = ${state.javaClass}")
            }
            is AlgState.Calibration -> {
                val mean = imageProcessing.processImage(image)
                if (imageProcessing.isFingerInPlace(mean)) {
                    values += mean[1]
                    timeStamps += System.currentTimeMillis()
                } else {
                    values.clear()
                    timeStamps.clear()
                    progress = 0f
                    startTime = System.currentTimeMillis()
                }
            }
            is AlgState.Register -> {
                values += imageProcessing.processImage(image)[1]
                timeStamps += System.currentTimeMillis()
            }
        }

        if (algState is AlgState.Calibration || algState is AlgState.Register) {
            updateState(System.currentTimeMillis())
        }
    }

    suspend fun beginStudy(context: Context, vibrator: Vibrator, onResult: (List<Long>, List<Double>) -> Unit) {
        this.onResult = onResult
        this.vibrator = vibrator
        startTime = System.currentTimeMillis()
        cameraLifecycle.doOnStart()
        prepareCamera(context)
        algState = AlgState.Calibration(false)
    }

    suspend fun dismissStudy(context: Context) {
        algState = AlgState.NONE
        cameraLifecycle.doOnDestroy()
        context.getCameraProvider().unbindAll()

        startTime = 0L
        progress = 0f
        timeStamps.clear()
        values.clear()
    }

    suspend fun finishStudy(context: Context) {
        progress = 1f
        algState = AlgState.Finished
        cameraLifecycle.doOnDestroy()
        context.getCameraProvider().unbindAll()
    }

    fun setResult(pulse: Int) {
        algState = AlgState.Result(pulse)
    }

    private fun updateState(lastTime: Long) {
        (lastTime - startTime).let {
            if (algState is AlgState.Calibration && it > CALIBRATION_TIME) {
                algState = AlgState.Register
                values.clear()
                timeStamps.clear()
                vibrator?.vibrate(VibrationEffect.createOneShot(400, 3))
            }
            if (algState is AlgState.Register && it > REGISTRATION_TIME + CALIBRATION_TIME) {
                this.onResult(timeStamps, values)
            }
            if (it > 200L) {
                progress = (it - 100L).toFloat() / (REGISTRATION_TIME + CALIBRATION_TIME - 200L).toFloat()
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
}