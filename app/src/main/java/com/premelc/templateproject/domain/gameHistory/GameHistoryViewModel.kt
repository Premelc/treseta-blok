package com.premelc.templateproject.domain.gameHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.premelc.templateproject.service.TresetaService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class GameHistoryViewModel(
    tresetaService: TresetaService,
    gameId: Int,
    private val navController: NavController,
) : ViewModel() {

    val viewState =
        tresetaService.selectedGameFlow(gameId).flatMapLatest {
            MutableStateFlow(GameHistoryViewState(it.setList))
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            GameHistoryViewState(emptyList()),
        )

    fun onInteraction(interaction: GameHistoryInteraction) {
        when (interaction) {
            GameHistoryInteraction.OnBackButtonClicked -> navController.popBackStack()
        }
    }
}