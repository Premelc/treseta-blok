package com.premelc.templateproject

import android.app.Application
import com.premelc.templateproject.networking.ApiClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
        ApiClient.initRetrofit(context = applicationContext)
    }
}