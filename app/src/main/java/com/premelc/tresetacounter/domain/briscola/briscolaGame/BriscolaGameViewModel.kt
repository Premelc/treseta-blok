package com.premelc.tresetacounter.domain.briscola.briscolaGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premelc.tresetacounter.service.BriscolaService
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

class BriscolaGameViewModel(
    private val briscolaService: BriscolaService
) : ViewModel() {

    init {
        viewModelScope.launch {
            if (briscolaService.selectedGameFlow().first() is GameState.NoActiveGames) {
                briscolaService.startNewGame()
            }
        }
    }

    private val currentSetId = MutableStateFlow(0)

    internal val viewState =
        briscolaService.selectedGameFlow().map { game ->
            when (game) {
                GameState.NoActiveGames -> {
                    BriscolaGameViewState.GameReady(
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
                    BriscolaGameViewState.GameReady(
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
            BriscolaGameViewState.GameLoading
        )

    private suspend fun GameState.GameReady.checkIfSetIsOver(roundsList: List<Round>) {
        if (roundsList.sumOf { it.firstTeamPoints } >= 41 || roundsList.sumOf { it.secondTeamPoints } >= 41) {
            briscolaService.updateCurrentGame(
                if (roundsList.sumOf { it.firstTeamPoints } > roundsList.sumOf { it.secondTeamPoints }) Team.FIRST
                else if (roundsList.sumOf { it.secondTeamPoints } > roundsList.sumOf { it.firstTeamPoints }) Team.SECOND
                else Team.NONE,
                this
            )
        }
    }

    internal fun onInteraction(interaction: BriscolaGameInteraction) {
        when (interaction) {
            BriscolaGameInteraction.TapOnBackButton -> Unit
            BriscolaGameInteraction.TapOnNewRound -> Unit
            BriscolaGameInteraction.TapOnHistoryButton -> Unit
            BriscolaGameInteraction.TapOnMenuButton -> Unit
            is BriscolaGameInteraction.TapOnRoundScore -> Unit
        }
    }

}