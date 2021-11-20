package pl.pw.mierzopuls

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import pl.pw.mierzopuls.alg.ImageProcessing

class MierzoPulsApp : Application() {

    private val helpersModule = module {
        single { ImageProcessing() }
    }

    private val repositoriesModule = module {

    }

    override fun onCreate() {
        super.onCreate()

        GlobalContext.startKoin {
            printLogger()
            androidContext(applicationContext)
            modules(helpersModule)
            modules(repositoriesModule)
        }
    }
}