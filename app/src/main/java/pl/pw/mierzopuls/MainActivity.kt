package pl.pw.mierzopuls

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.camera2.Camera2Config
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import pl.pw.mierzopuls.ui.Home
import pl.pw.mierzopuls.ui.theme.MierzoPulsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MierzoPulsTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top) {
                        Home(onPermissionHandler = { permission ->
                            permissionHandler.launch(permission)
                        })
                    }
                }
            }
        }
    }
    val permissionHandler = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) onStart() else {
            Toast.makeText(this, "Enable camera in order to use pulse meter !", Toast.LENGTH_LONG).show()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MierzoPulsTheme {
        Surface(color = MaterialTheme.colors.background) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top) {
                Home { }
            }
        }
    }
}