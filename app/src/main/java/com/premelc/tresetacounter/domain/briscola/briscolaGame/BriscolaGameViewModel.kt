package com.premelc.tresetacounter.domain.briscola.briscolaGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premelc.tresetacounter.service.BriscolaService
import com.premelc.tresetacounter.service.data.GameState
import com.premelc.tresetacounter.service.data.Round
import com.premelc.tresetacounter.utils.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BriscolaGameViewModel(
    private val briscolaService: BriscolaService
) : ViewModel() {

    private val currentSetId = MutableStateFlow(0)
    private val setFinishedModalFlow = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            briscolaService.selectedGameFlow().filterIsInstance<GameState.GameReady>().collectLatest { game ->
                checkIfSetIsOver(game.setList.firstOrNull()?.roundsList ?: emptyList())
            }
        }
    }

    internal val viewState =
        combine(
            briscolaService.selectedGameFlow(),
            setFinishedModalFlow,
        ) { game, showSetFinishedModal ->
            when (game) {
                GameState.NoActiveGames -> {
                    BriscolaGameViewState.GameLoading
                }

                is GameState.GameReady -> {
                    currentSetId.value = game.setList.firstOrNull()?.id ?: 0
                    BriscolaGameViewState.GameReady(
                        rounds = game.setList.firstOrNull()?.roundsList ?: emptyList(),
                        firstTeamScore = game.firstTeamScore,
                        secondTeamScore = game.secondTeamScore,
                        winningTeam = game.setList.first().roundsList.checkWinningTeam(),
                        showHistoryButton = game.setList.any { it.roundsList.isNotEmpty() },
                        currentSetId = currentSetId.value,
                        showSetFinishedModal = showSetFinishedModal,
                    )
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            BriscolaGameViewState.GameLoading
        )

    private fun checkIfSetIsOver(roundsList: List<Round>) {
        if (roundsList.sumOf { it.firstTeamPoints } >= 4 || roundsList.sumOf { it.secondTeamPoints } >= 4) {
            setFinishedModalFlow.value = true
        }
    }

    private fun List<Round>.checkWinningTeam(): Team {
        val firstTeamPoints = this.sumOf { it.firstTeamPoints }
        val secondTeamPoints = this.sumOf { it.secondTeamPoints }
        return when {
            firstTeamPoints > secondTeamPoints -> Team.FIRST
            secondTeamPoints > firstTeamPoints -> Team.SECOND
            else -> Team.NONE
        }
    }

    internal fun onInteraction(interaction: BriscolaGameInteraction) {
        when (interaction) {
            BriscolaGameInteraction.TapOnBackButton -> Unit
            is BriscolaGameInteraction.TapOnAddPointButton -> {
                viewModelScope.launch {
                    briscolaService.insertRound(
                        setId = currentSetId.value,
                        firstTeamPoints = if (interaction.team == Team.FIRST) 1 else 0,
                        secondTeamPoints = if (interaction.team == Team.SECOND) 1 else 0,
                    )
                }
            }

            BriscolaGameInteraction.TapOnMenuButton -> Unit
            BriscolaGameInteraction.TapOnSetFinishedModalConfirm -> {
                viewModelScope.launch {
                    briscolaService.selectedGameFlow().filterIsInstance<GameState.GameReady>().first().let { game ->
                        val roundsList = game.setList.firstOrNull()?.roundsList ?: emptyList()
                        briscolaService.updateCurrentGame(
                            if (roundsList.sumOf { it.firstTeamPoints } > roundsList.sumOf { it.secondTeamPoints }) Team.FIRST
                            else if (roundsList.sumOf { it.secondTeamPoints } > roundsList.sumOf { it.firstTeamPoints }) Team.SECOND
                            else Team.NONE,
                            game
                        )
                    }
                    setFinishedModalFlow.value = false
                }
            }
        }
    }

}