package com.premelc.tresetacounter.domain.briscola.briscolaGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premelc.tresetacounter.service.BriscolaService
import com.premelc.tresetacounter.service.data.BriscolaGameSet
import com.premelc.tresetacounter.service.data.BriscolaGameState
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
            briscolaService.selectedGameFlow().filterIsInstance<BriscolaGameState.GameReady>().collectLatest { game ->
                game.setList.firstOrNull()?.let {
                    checkIfSetIsOver(it)
                }
            }
        }
    }

    internal val viewState = combine(
        briscolaService.selectedGameFlow(),
        setFinishedModalFlow,
    ) { game, showSetFinishedModal ->
        when (game) {
            BriscolaGameState.NoActiveGames -> BriscolaGameViewState.GameLoading
            is BriscolaGameState.GameReady -> {
                game.setList.firstOrNull()?.let { activeSet ->
                    currentSetId.value = activeSet.id
                    BriscolaGameViewState.GameReady(
                        firstTeamScore = game.firstTeamScore,
                        firstTeamCurrentSetScore = activeSet.firstTeamPoints,
                        secondTeamScore = game.secondTeamScore,
                        secondTeamCurrentSetScore = activeSet.secondTeamPoints,
                        winningTeam = checkWinningTeam(activeSet),
                        currentSetId = currentSetId.value,
                        showSetFinishedModal = showSetFinishedModal,
                    )
                } ?: BriscolaGameViewState.GameLoading
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        BriscolaGameViewState.GameLoading
    )

    private fun checkIfSetIsOver(set: BriscolaGameSet) {
        if (set.firstTeamPoints >= 4 || set.secondTeamPoints >= 4) {
            setFinishedModalFlow.value = true
        }
    }

    internal fun onInteraction(interaction: BriscolaGameInteraction) {
        when (interaction) {
            is BriscolaGameInteraction.TapOnAddPointButton -> {
                viewModelScope.launch {
                    briscolaService.addPointToTeam(currentSetId.value, interaction.team)
                }
            }

            BriscolaGameInteraction.TapOnSetFinishedModalConfirm -> {
                viewModelScope.launch {
                    briscolaService.selectedGameFlow().filterIsInstance<BriscolaGameState.GameReady>().first().let { game ->
                        game.setList.firstOrNull()?.let {
                            briscolaService.updateCurrentGame(
                                winningTeam = checkWinningTeam(it),
                                game = game
                            )
                        }
                    }
                    setFinishedModalFlow.value = false
                }
            }
            is BriscolaGameInteraction.TapOnSubtractPointButton -> {
                viewModelScope.launch {
                    briscolaService.removePointFromTeam(currentSetId.value, interaction.team)
                }
            }
            BriscolaGameInteraction.TapOnMenuButton -> Unit
        }
    }

    private fun checkWinningTeam(set: BriscolaGameSet) = if (set.firstTeamPoints > set.secondTeamPoints) Team.FIRST else Team.SECOND
}