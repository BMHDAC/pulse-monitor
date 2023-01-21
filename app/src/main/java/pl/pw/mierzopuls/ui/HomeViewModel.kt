package pl.pw.mierzopuls.ui

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import pl.pw.mierzopuls.alg.*
import pl.pw.mierzopuls.model.*

class HomeViewModel(
    application: Application,
    val appSetting: AppSetting,
    private val studyManager: StudyManager
) : AndroidViewModel(application) {
    private val studyRepository: StudyRepository by inject(StudyRepository::class.java)

    var studies: List<Study> by mutableStateOf(listOf())
    val algState: AlgState
        get() = studyManager.algState
    val studyProgress
        get() = studyManager.progress

    // TODO: proper functioning
    var openInstruction by mutableStateOf(appSetting.showInstructionOnStart)
    var isLoading by mutableStateOf(true)

    init {
        viewModelScope.launch {
            val lastTime = System.currentTimeMillis()
            viewModelScope.launch {
                studies = studyRepository.readStudies(getApplication())?.sortByDate() ?: listOf()
            }
            if (System.currentTimeMillis() - lastTime < 300L) {
                delay(System.currentTimeMillis() - lastTime)
            }
            isLoading = false
        }
    }

    fun beginStudy(launcher: ManagedActivityResultLauncher<String, Boolean>) {
        if (!checkPermissions(launcher)) return
        viewModelScope.launch {
            studyManager.beginStudy(
                getApplication(),
                getApplication<Application>().applicationContext.getSystemService()!!
            ) { timeStamps, values ->
                finishStudy(timeStamps, values)
            }
        }
    }

    fun dismissStudy() {
        viewModelScope.launch {
            studyManager.dismissStudy(getApplication())
        }
    }

    private fun finishStudy(timeStamps: List<Long>, values: List<Double>) {
        viewModelScope.launch {
            studyManager.finishStudy(getApplication())
            vibrate()
        }
        val timeStampsOffSetFromZero = timeStamps.map { (it - timeStamps[0]).toInt() }
        processSignal(values.toList(), timeStampsOffSetFromZero).let { study ->
            studies =  study + studies
            studyRepository.save(getApplication(), study)
            studyManager.setResult(study.pulse)
            sendEvent(getApplication(), study)
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

    private fun vibrate() {
        val v = getApplication<Application>().applicationContext.getSystemService<Vibrator>()
        v!!.vibrate(400)
    }
}