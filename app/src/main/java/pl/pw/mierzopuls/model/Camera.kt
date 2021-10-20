package pl.pw.mierzopuls.model

import android.content.Context
import androidx.camera.core.*
import androidx.camera.core.Camera
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class Camera(
    private val context: Context,
    var cameraSelector : CameraSelector = CameraSelector.Builder()
                     .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                     .build()) : LifecycleOwner {
    lateinit var camera: Camera
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    init {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    fun bindPreview(previewView: PreviewView) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            camera = cameraProvider.bindToLifecycle(
                this as LifecycleOwner, cameraSelector, preview)

            preview.setSurfaceProvider(previewView.surfaceProvider)
        }, ContextCompat.getMainExecutor(context))
    }

    fun start() {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    fun stop() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
}