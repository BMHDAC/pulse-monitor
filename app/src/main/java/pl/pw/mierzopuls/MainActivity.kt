package pl.pw.mierzopuls

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.koin.androidx.viewmodel.ext.android.getViewModel
import pl.pw.mierzopuls.ui.Home
import pl.pw.mierzopuls.ui.HomeViewModel
import pl.pw.mierzopuls.ui.theme.MierzoPulsTheme

class MainActivity : ComponentActivity() {
    @ExperimentalPermissionsApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm: HomeViewModel = getViewModel()
        installSplashScreen().apply {
            this.setKeepOnScreenCondition {
                vm.isLoading
            }
        }
        setContent {
            MierzoPulsTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Home()
                }
            }
        }
    }
}