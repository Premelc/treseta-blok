package com.premelc.templateproject.service.quotesService

sealed interface QuotesServiceState {
    data class Available(
        val quotes: List<Quote>
    ) : QuotesServiceState

    object Unavailable : QuotesServiceState
    object Loading : QuotesServiceState
}