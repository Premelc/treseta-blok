package com.premelc.templateproject

import com.premelc.templateproject.domain.mainMenu.MainMenuViewModel
import com.premelc.templateproject.domain.tresetaGameCounter.TresetaGameViewModel
import com.premelc.templateproject.domain.gameCalculator.GameCalculatorViewModel
import com.premelc.templateproject.service.quotesService.QuotesService
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::QuotesService)
    viewModelOf(::MainMenuViewModel)
    viewModelOf(::TresetaGameViewModel)
    viewModelOf(::GameCalculatorViewModel)
}