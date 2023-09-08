package com.premelc.templateproject.domain.mainMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premelc.templateproject.service.quotesService.QuotesService
import com.premelc.templateproject.service.quotesService.QuotesServiceState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainMenuViewModel(
    private val quotesService: QuotesService,
) : ViewModel() {

    internal fun onInteraction(interaction: MainMenuInteraction){
        when(interaction){
            MainMenuInteraction.OnNewGameClicked -> {

            }
        }
    }
}