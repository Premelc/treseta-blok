package com.premelc.templateproject.domain.tresetaGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.premelc.templateproject.data.TresetaDatabase
import com.premelc.templateproject.domain.gameCalculator.Team
import com.premelc.templateproject.navigation.NavRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class TresetaGameViewModel(
    tresetaDatabase: TresetaDatabase,
    private val navController: NavController,
    private val gameId: Int,
) : ViewModel() {

    private val firstTeamPointsFlow = MutableStateFlow(0)
    private val secondTeamPointsFlow = MutableStateFlow(0)

    internal val viewState =
        combine(
            tresetaDatabase.roundDao().getAllRounds(gameId),
            firstTeamPointsFlow,
            secondTeamPointsFlow,
        ) { rounds, firstTeamPoints, secondTeamPoints ->
            firstTeamPointsFlow.value = rounds.sumOf { it.firstTeamPoints }
            secondTeamPointsFlow.value = rounds.sumOf { it.secondTeamPoints }
            TresetaGameViewState(
                rounds = rounds,
                firstTeamScore = firstTeamPoints,
                secondTeamScore = secondTeamPoints,
                winningTeam = when {
                    firstTeamPoints > secondTeamPoints -> Team.FIRST
                    secondTeamPoints > firstTeamPoints -> Team.SECOND
                    else -> Team.NONE
                }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            TresetaGameViewState()
        )

    internal fun onInteraction(interaction: TresetaGameInteraction) {
        when (interaction) {
            TresetaGameInteraction.TapOnNewRound -> {
                navController.navigate(NavRoutes.GameCalculator.route.plus("/${gameId}"))
            }

            TresetaGameInteraction.TapOnBackButton -> {
                navController.popBackStack()
            }
        }
    }

}