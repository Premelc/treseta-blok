package com.premelc.tresetacounter

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.premelc.tresetacounter.utils.LocaleHelper.updateResources
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        updateResources(this)
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(updateResources(base!!))
    }

    override fun onConfigurationChanged(config: Configuration) {
        super.onConfigurationChanged(config)
        updateResources(this)
    }
}
