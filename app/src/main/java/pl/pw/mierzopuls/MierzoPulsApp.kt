package pl.pw.mierzopuls

import android.app.Application
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.inject
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import pl.pw.mierzopuls.alg.ImageProcessing
import pl.pw.mierzopuls.model.AppSetting
import pl.pw.mierzopuls.model.StudyRepository
import pl.pw.mierzopuls.ui.History
import pl.pw.mierzopuls.ui.Home
import pl.pw.mierzopuls.ui.HomeViewModel
import pl.pw.mierzopuls.util.CameraLifecycle

class MierzoPulsApp : Application() {

    private val utilModule = module {
        single { CameraLifecycle() }
        single { ImageProcessing() }
    }

    private val repositoriesModule = module {
        single { StudyRepository() }
        single { AppSetting(androidContext()) }
    }

    private val viewModelModule = module {
        single { HomeViewModel(androidApplication()) }
    }

    override fun onCreate() {
        super.onCreate()

        GlobalContext.startKoin {
            androidContext(applicationContext)
            modules(utilModule)
            modules(repositoriesModule)
            modules(viewModelModule)
        }
    }
}

@ExperimentalPermissionsApi
@ExperimentalFoundationApi
@Composable
fun app() {
    val viewModel: HomeViewModel by inject()
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home(navController)
        }
        composable("history") {
            History()
        }
    }
    LaunchedEffect(key1 = viewModel) {
        viewModel.initRepository()
    }
}