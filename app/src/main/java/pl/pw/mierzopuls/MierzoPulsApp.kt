package pl.pw.mierzopuls

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import pl.pw.mierzopuls.alg.ImageProcessing
import pl.pw.mierzopuls.model.Study
import pl.pw.mierzopuls.ui.History
import pl.pw.mierzopuls.ui.Home

class MierzoPulsApp : Application() {

    private val helpersModule = module {
        single { ImageProcessing() }
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

@Composable
fun app() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home(navController)
        }
        composable("history") {
            History(listOf(Study("id1"), Study("id2"), Study("id3")))
        }
    }
}