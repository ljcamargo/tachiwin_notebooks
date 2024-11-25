package org.nehuatl.tachiwin.core

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import org.nehuatl.tachiwin.viewmodels.ContextBearer
import org.nehuatl.tachiwin.viewmodels.DownloadViewModel
import org.nehuatl.tachiwin.viewmodels.MainViewModel

class Application: Application() {

    private val modules = module {
        single { ContextBearer(androidContext()) }
        single { Preferences(androidContext()) }
        single { DownloadViewModel(androidContext()) }
        single { MainViewModel() }
        single { Logger(androidContext()) }
    }

    override fun onCreate() {
        super.onCreate()
        GlobalContext.startKoin {
            androidContext(this@Application)
            modules(modules)
        }
    }
}