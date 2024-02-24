package com.premelc.tresetacounter.domain.treseta.tresetaGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premelc.tresetacounter.service.TresetaService
import com.premelc.tresetacounter.service.data.Round
import com.premelc.tresetacounter.service.data.TresetaGameState
import com.premelc.tresetacounter.utils.Team
import com.premelc.tresetacounter.utils.checkWinningTeam
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TresetaGameViewModel(private val tresetaService: TresetaService) : ViewModel() {

    private val currentSetId = MutableStateFlow(0)
    private val setFinishedModalFlow = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            tresetaService.selectedGameFlow().filterIsInstance<TresetaGameState.GameReady>()
                .collectLatest { game ->
                    checkIfSetIsOver(game.setList.firstOrNull()?.roundsList ?: emptyList())
                }
        }
    }

    internal val viewState = combine(
        tresetaService.selectedGameFlow(),
        setFinishedModalFlow,
    ) { game, setFinishedModal ->
        when (game) {
            TresetaGameState.NoActiveGames -> TresetaGameViewState.GameLoading

            is TresetaGameState.GameReady -> {
                currentSetId.value = game.setList.firstOrNull()?.id ?: 0
                TresetaGameViewState.GameReady(
                    rounds = game.setList.firstOrNull()?.roundsList ?: emptyList(),
                    firstTeamScore = game.firstTeamScore,
                    secondTeamScore = game.secondTeamScore,
                    winningTeam = game.setList.firstOrNull()?.roundsList?.checkWinningTeam()
                        ?: Team.NONE,
                    showHistoryButton = game.setList.any { it.roundsList.isNotEmpty() },
                    currentSetId = currentSetId.value,
                    showSetFinishedModal = setFinishedModal,
                )
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        TresetaGameViewState.GameLoading
    )

    private fun checkIfSetIsOver(roundsList: List<Round>) {
        if (roundsList.sumOf { it.firstTeamPoints } >= 41 || roundsList.sumOf { it.secondTeamPoints } >= 41) {
            setFinishedModalFlow.value = true
        }
    }

    internal fun onInteraction(interaction: TresetaGameInteraction) {
        when (interaction) {
            TresetaGameInteraction.TapOnBackButton -> Unit
            TresetaGameInteraction.TapOnNewRound -> Unit
            TresetaGameInteraction.TapOnHistoryButton -> Unit
            TresetaGameInteraction.TapOnMenuButton -> Unit
            is TresetaGameInteraction.TapOnRoundScore -> Unit
            TresetaGameInteraction.TapOnSetFinishedModalConfirm -> {
                viewModelScope.launch {
                    tresetaService.selectedGameFlow().filterIsInstance<TresetaGameState.GameReady>()
                        .first().let { game ->
                            val roundsList = game.setList.firstOrNull()?.roundsList ?: emptyList()
                            tresetaService.updateCurrentGame(
                                winningTeam = roundsList.getWinningTeam(),
                                game = game,
                            )
                        }
                    setFinishedModalFlow.value = false
                }
            }
        }
    }

    private fun List<Round>.getWinningTeam() =
        if (this.sumOf { it.firstTeamPoints } > this.sumOf { it.secondTeamPoints }) {
            Team.FIRST
        } else if (this.sumOf { it.secondTeamPoints } > this.sumOf { it.firstTeamPoints }) {
            Team.SECOND
        } else {
            Team.NONE
        }
}
