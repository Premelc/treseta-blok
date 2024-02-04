package com.premelc.tresetacounter

import androidx.room.Room
import com.premelc.tresetacounter.data.TresetaDatabase
import com.premelc.tresetacounter.domain.mainMenu.MainMenuViewModel
import com.premelc.tresetacounter.domain.tresetaGame.TresetaGameViewModel
import com.premelc.tresetacounter.domain.gameCalculator.GameCalculatorViewModel
import com.premelc.tresetacounter.domain.roundEdit.RoundEditViewModel
import com.premelc.tresetacounter.domain.gameHistory.GameHistoryViewModel
import com.premelc.tresetacounter.service.TresetaService
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            TresetaDatabase::class.java,
            "treseta_db"
        ).fallbackToDestructiveMigration()
            .build()
    }
    viewModelOf(::MainMenuViewModel)
    viewModelOf(::TresetaGameViewModel)
    viewModelOf(::GameCalculatorViewModel)
    viewModelOf(::GameHistoryViewModel)
    viewModelOf(::RoundEditViewModel)
    singleOf(::TresetaService)
}