package pl.pw.mierzopuls

import android.app.Application
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import pl.pw.mierzopuls.alg.ImageProcessing
import pl.pw.mierzopuls.model.Study
import pl.pw.mierzopuls.ui.DebugView
import pl.pw.mierzopuls.ui.History
import pl.pw.mierzopuls.ui.Home
import pl.pw.mierzopuls.ui.HomeViewModel

class MierzoPulsApp : Application() {

    private val helpersModule = module {
    }

    private val repositoriesModule = module {

    }

    override fun onCreate() {
        super.onCreate()

        GlobalContext.startKoin {
            androidContext(applicationContext)
            modules(helpersModule)
            modules(repositoriesModule)
        }
    }
}

@ExperimentalPermissionsApi
@ExperimentalFoundationApi
@Composable
fun app() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val viewModel = HomeViewModel(context)
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home(navController)
        }
        composable("history") {
            History(viewModel.studies.value)
        }
        composable("debug") {
            DebugView()
        }
    }
}