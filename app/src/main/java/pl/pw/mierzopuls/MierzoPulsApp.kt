package pl.pw.mierzopuls

import android.app.Application
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import pl.pw.mierzopuls.model.alg.ImageProcessing
import pl.pw.mierzopuls.model.alg.StudyManager
import pl.pw.mierzopuls.model.AppSetting
import pl.pw.mierzopuls.model.StudyRepository
import pl.pw.mierzopuls.ui.HomeViewModel
import pl.pw.mierzopuls.util.CameraLifecycle

class MierzoPulsApp : Application() {

    private val utilModule = module {
        single { CameraLifecycle() }
        single { ImageProcessing }
    }

    private val repositoriesModule = module {
        single { StudyRepository() }
        single { AppSetting(androidContext()) }
    }

    private val appModule = module {
        single { StudyManager(get(), get()) }
        viewModel { HomeViewModel(androidApplication(), get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()

        GlobalContext.startKoin {
            androidContext(applicationContext)
            modules(utilModule)
            modules(repositoriesModule)
            modules(appModule)
        }
    }
}