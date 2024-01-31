package com.premelc.tresetacounter.domain.gameHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.premelc.tresetacounter.service.TresetaService
import com.premelc.tresetacounter.service.data.GameState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class GameHistoryViewModel(
    tresetaService: TresetaService,
    private val navController: NavController,
) : ViewModel() {

    val viewState =
        tresetaService.selectedGameFlow().flatMapLatest {
            if (it is GameState.GameReady) {
                MutableStateFlow(
                    GameHistoryViewState(
                        firstTeamScore = it.firstTeamScore,
                        secondTeamScore = it.secondTeamScore,
                        sets = it.setList,
                    ),
                )
            } else {
                MutableStateFlow(GameHistoryViewState())
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            GameHistoryViewState(),
        )

    fun onInteraction(interaction: GameHistoryInteraction) {
        when (interaction) {
            GameHistoryInteraction.OnBackButtonClicked -> navController.popBackStack()
        }
    }
}