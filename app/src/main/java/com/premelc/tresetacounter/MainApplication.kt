package com.premelc.tresetacounter

import android.app.Application
import com.premelc.tresetacounter.networking.ApiClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(com.premelc.tresetacounter.appModule)
        }
        ApiClient.initRetrofit(context = applicationContext)
    }
}