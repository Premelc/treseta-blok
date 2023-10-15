package com.premelc.templateproject.domain.tresetaGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.premelc.templateproject.domain.gameCalculator.Team
import com.premelc.templateproject.navigation.NavRoutes
import com.premelc.templateproject.service.TresetaService
import com.premelc.templateproject.service.data.GameState
import com.premelc.templateproject.service.data.Round
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TresetaGameViewModel(
    private val tresetaService: TresetaService,
    private val navController: NavController,
    gameId: Int,
) : ViewModel() {
    private val currentSetId = MutableStateFlow(0)

    internal val viewState =
        tresetaService.selectedGameFlow(gameId).map { game ->
            currentSetId.value = game.setList.first().id
            game.checkIfSetIsOver(game.setList.first().roundsList)
            TresetaGameViewState(
                rounds = game.setList.first().roundsList,
                firstTeamScore = game.firstTeamScore,
                secondTeamScore = game.secondTeamScore,
                winningTeam = when {
                    game.firstTeamScore > game.secondTeamScore -> Team.FIRST
                    game.secondTeamScore > game.firstTeamScore -> Team.SECOND
                    else -> Team.NONE
                }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            TresetaGameViewState()
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
        }
    }

}