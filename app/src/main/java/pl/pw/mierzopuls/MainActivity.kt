package pl.pw.mierzopuls

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import pl.pw.mierzopuls.ui.theme.MierzoPulsTheme

class MainActivity : ComponentActivity() {
    @ExperimentalPermissionsApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MierzoPulsTheme {
                Surface(color = MaterialTheme.colors.background) {
                    app()
                }
            }
        }
    }
}