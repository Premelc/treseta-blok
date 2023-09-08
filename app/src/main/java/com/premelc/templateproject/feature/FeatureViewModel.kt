package com.premelc.templateproject.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.premelc.templateproject.service.quotesService.QuotesService
import com.premelc.templateproject.service.quotesService.QuotesServiceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

import kotlinx.coroutines.launch

class FeatureViewModel(
    private val quotesService: QuotesService,
) : ViewModel() {
    val viewState = quotesService.getQuotesStateFlow().map { state ->
        when (state) {
            is QuotesServiceState.Available -> FeatureState.Available(state.quotes)
            QuotesServiceState.Loading -> FeatureState.Loading
            QuotesServiceState.Unavailable -> FeatureState.Unavailable
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = FeatureState.Loading
    )

    fun onRetry(){
        quotesService.refreshQuotes()
    }
}