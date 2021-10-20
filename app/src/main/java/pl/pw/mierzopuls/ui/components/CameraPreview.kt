package pl.pw.mierzopuls.ui.components

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import pl.pw.mierzopuls.model.Camera

@Composable
fun CameraPreview(camera: Camera,
                  modifier: Modifier = Modifier
                      .width(400.dp)
                      .height(300.dp),
                  ) {
    Box(modifier = modifier) {
        AndroidView(
            factory = { context ->
                PreviewView(context).also {
                    camera.bindPreview(it)
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}