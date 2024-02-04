package com.premelc.tresetacounter.domain.tresetaGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premelc.tresetacounter.service.TresetaService
import com.premelc.tresetacounter.service.data.GameState
import com.premelc.tresetacounter.service.data.Round
import com.premelc.tresetacounter.utils.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TresetaGameViewModel(private val tresetaService: TresetaService) : ViewModel() {

    init {
        viewModelScope.launch {
            if (tresetaService.selectedGameFlow().first() is GameState.NoActiveGames) {
                tresetaService.startNewGame()
            }
        }
    }

    private val currentSetId = MutableStateFlow(0)

    internal val viewState =
        tresetaService.selectedGameFlow().map { game ->
            when (game) {
                GameState.NoActiveGames -> {
                    TresetaGameViewState.GameReady(
                        rounds = emptyList(),
                        firstTeamScore = 0,
                        secondTeamScore = 0,
                        winningTeam = Team.NONE,
                        showHistoryButton = true
                    )
                }

                is GameState.GameReady -> {
                    currentSetId.value = game.setList.firstOrNull()?.id ?: 0
                    game.checkIfSetIsOver(game.setList.firstOrNull()?.roundsList ?: emptyList())
                    TresetaGameViewState.GameReady(
                        rounds = game.setList.firstOrNull()?.roundsList ?: emptyList(),
                        firstTeamScore = game.firstTeamScore,
                        secondTeamScore = game.secondTeamScore,
                        winningTeam = when {
                            game.firstTeamScore > game.secondTeamScore -> Team.FIRST
                            game.secondTeamScore > game.firstTeamScore -> Team.SECOND
                            else -> Team.NONE
                        },
                        showHistoryButton = game.setList.any { it.roundsList.isNotEmpty() },
                        currentSetId = currentSetId.value,
                    )
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            TresetaGameViewState.GameLoading
        )

    private suspend fun GameState.GameReady.checkIfSetIsOver(roundsList: List<Round>) {
        if (roundsList.sumOf { it.firstTeamPoints } >= 41 || roundsList.sumOf { it.secondTeamPoints } >= 41) {
            tresetaService.updateCurrentGame(
                if (roundsList.sumOf { it.firstTeamPoints } > roundsList.sumOf { it.secondTeamPoints }) Team.FIRST
                else if (roundsList.sumOf { it.secondTeamPoints } > roundsList.sumOf { it.firstTeamPoints }) Team.SECOND
                else Team.NONE,
                this
            )
        }
    }

    internal fun onInteraction(interaction: TresetaGameInteraction) {
        when (interaction) {
            TresetaGameInteraction.TapOnBackButton -> Unit
            TresetaGameInteraction.TapOnNewRound -> Unit
            TresetaGameInteraction.TapOnHistoryButton -> Unit
            TresetaGameInteraction.TapOnMenuButton -> Unit
            is TresetaGameInteraction.TapOnRoundScore -> Unit
        }
    }

}