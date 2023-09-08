package com.premelc.templateproject

import com.premelc.templateproject.feature.FeatureViewModel
import com.premelc.templateproject.service.quotesService.QuotesService

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::QuotesService)
    viewModelOf(::FeatureViewModel)
}