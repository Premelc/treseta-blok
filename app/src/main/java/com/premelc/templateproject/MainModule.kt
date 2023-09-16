package com.premelc.templateproject

import androidx.room.Room
import com.premelc.templateproject.data.TresetaDatabase
import com.premelc.templateproject.domain.mainMenu.MainMenuViewModel
import com.premelc.templateproject.domain.tresetaGame.TresetaGameViewModel
import com.premelc.templateproject.domain.gameCalculator.GameCalculatorViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
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
    viewModel { params ->
        TresetaGameViewModel(
            tresetaDatabase = get(),
            navController = params[0],
            gameId = params[1],
        )
    }
    viewModelOf(::GameCalculatorViewModel)
}