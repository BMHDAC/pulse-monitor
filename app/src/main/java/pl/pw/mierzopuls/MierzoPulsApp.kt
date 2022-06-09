package pl.pw.mierzopuls

import android.app.Application
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import pl.pw.mierzopuls.alg.ImageProcessing
import pl.pw.mierzopuls.model.StudyRepository
import pl.pw.mierzopuls.ui.DebugView
import pl.pw.mierzopuls.ui.History
import pl.pw.mierzopuls.ui.Home
import pl.pw.mierzopuls.ui.HomeViewModel
import pl.pw.mierzopuls.util.CameraLifecycle

class MierzoPulsApp : Application() {

    private val processingModule = module {
        single { CameraLifecycle() }
        single { ImageProcessing() }
    }

    private val repositoriesModule = module {
        single { StudyRepository() }
    }

    override fun onCreate() {
        super.onCreate()

        GlobalContext.startKoin {
            androidContext(applicationContext)
            modules(processingModule)
            modules(repositoriesModule)
        }
    }
}

@ExperimentalPermissionsApi
@ExperimentalFoundationApi
@Composable
fun app() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val viewModel = HomeViewModel(context, lifecycleOwner, navController, coroutineScope)
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home(viewModel)
        }
        composable("history") {
            //History(viewModel.studies.value)
        }
        composable("debug") {
           // DebugView()
        }
    }
}