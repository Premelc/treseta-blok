package com.premelc.templateproject.domain.tresetaGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.premelc.templateproject.domain.gameCalculator.Team
import com.premelc.templateproject.navigation.NavRoutes
import com.premelc.templateproject.service.TresetaService
import com.premelc.templateproject.service.data.GameState
import com.premelc.templateproject.service.data.Round
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TresetaGameViewModel(
    private val tresetaService: TresetaService,
    private val navController: NavController,
) : ViewModel() {

    init {
        viewModelScope.launch {
            if (tresetaService.selectedGameFlow().first() is GameState.NoActiveGames){
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
                        showHistoryButton = game.setList.any { it.roundsList.isNotEmpty() }
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
            TresetaGameInteraction.TapOnBackButton -> {
                navController.popBackStack()
            }

            TresetaGameInteraction.TapOnNewRound -> {
                navController.navigate(NavRoutes.GameCalculator.route.plus("/${currentSetId.value}"))
            }

            TresetaGameInteraction.TapOnHistoryButton -> navController.navigate(NavRoutes.GameHistory.route)
            TresetaGameInteraction.TapOnMenuButton -> navController.navigate(NavRoutes.MainMenu.route)
        }
    }

}