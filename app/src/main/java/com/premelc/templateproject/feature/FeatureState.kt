package com.premelc.templateproject.feature

import com.premelc.templateproject.service.quotesService.Quote

sealed interface FeatureState {
    data class Available(
        val quotes: List<Quote>
    ): FeatureState
    object Loading : FeatureState
    object Unavailable : FeatureState
}