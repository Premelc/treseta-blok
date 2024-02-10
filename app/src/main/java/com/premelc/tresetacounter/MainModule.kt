package com.premelc.tresetacounter

import androidx.room.Room
import com.premelc.tresetacounter.data.CardGameDatabase
import com.premelc.tresetacounter.domain.mainMenu.MainMenuViewModel
import com.premelc.tresetacounter.domain.treseta.tresetaGame.TresetaGameViewModel
import com.premelc.tresetacounter.domain.briscola.briscolaGame.BriscolaGameViewModel
import com.premelc.tresetacounter.domain.treseta.tresetaCalculator.TresetaCalculatorViewModel
import com.premelc.tresetacounter.domain.briscola.briscolaCalculator.BriscolaCalculatorViewModel
import com.premelc.tresetacounter.domain.treseta.tresetaRoundEdit.TresetaRoundEditViewModel
import com.premelc.tresetacounter.domain.briscola.briscolaRoundEdit.BriscolaRoundEditViewModel
import com.premelc.tresetacounter.domain.treseta.tresetaHistory.TresetaHistoryViewModel
import com.premelc.tresetacounter.service.TresetaService
import com.premelc.tresetacounter.service.BriscolaService
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            CardGameDatabase::class.java,
            "cardgame_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    viewModelOf(::MainMenuViewModel)

    viewModelOf(::BriscolaGameViewModel)
    viewModelOf(::BriscolaRoundEditViewModel)
    viewModelOf(::BriscolaCalculatorViewModel)
    singleOf(::BriscolaService)

    viewModelOf(::TresetaGameViewModel)
    viewModelOf(::TresetaHistoryViewModel)
    viewModelOf(::TresetaRoundEditViewModel)
    viewModelOf(::TresetaCalculatorViewModel)
    singleOf(::TresetaService)
}