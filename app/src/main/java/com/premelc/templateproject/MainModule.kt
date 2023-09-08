package com.premelc.templateproject

import androidx.room.Room
import com.premelc.templateproject.data.TresetaDatabase
import com.premelc.templateproject.domain.mainMenu.MainMenuViewModel
import com.premelc.templateproject.domain.tresetaGameCounter.TresetaGameViewModel
import com.premelc.templateproject.domain.gameCalculator.GameCalculatorViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single{
        Room.databaseBuilder(
            androidApplication(),
            TresetaDatabase::class.java,
            "treseta_db"
        ).fallbackToDestructiveMigration()
            .build()
    }
    viewModelOf(::MainMenuViewModel)
    viewModelOf(::TresetaGameViewModel)
    viewModelOf(::GameCalculatorViewModel)
}