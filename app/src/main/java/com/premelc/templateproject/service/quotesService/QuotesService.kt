package com.premelc.templateproject.service.quotesService

import com.premelc.templateproject.data.QuoteListEntity
import com.premelc.templateproject.networking.ApiClient
import com.premelc.templateproject.service.apis.QuotesApi
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuotesService {

    private var quotesApi: QuotesApi = ApiClient.retrofit.create(QuotesApi::class.java)

    private val quotesStateFlow = MutableStateFlow<QuotesServiceState>(QuotesServiceState.Loading)

    fun getQuotesStateFlow(): StateFlow<QuotesServiceState> {
        if (quotesStateFlow.value == QuotesServiceState.Loading) {
            refreshQuotes()
        }
        return quotesStateFlow
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun refreshQuotes() {
        GlobalScope.launch {
            quotesStateFlow.value = QuotesServiceState.Loading
            quotesStateFlow.value = quotesApi.getQuotes().toQuotesState()
        }
    }

    private fun QuoteListEntity.toQuotesState(): QuotesServiceState =
        if (this.results.isNotEmpty()) {
            QuotesServiceState.Available(
                quotes = this.results.map {
                    Quote(
                        author = it.author,
                        content = it.content,
                    )
                }
            )
        } else {
            QuotesServiceState.Unavailable
        }
}