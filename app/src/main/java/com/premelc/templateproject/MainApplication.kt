package com.premelc.templateproject

import android.app.Application
import com.premelc.templateproject.networking.ApiClient
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(appModule)
        }
        ApiClient.initRetrofit(context = applicationContext)
    }
}