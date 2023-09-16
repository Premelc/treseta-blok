package com.premelc.templateproject.domain.gameCalculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.premelc.templateproject.data.RoundEntity
import com.premelc.templateproject.data.TresetaDatabase
import com.premelc.templateproject.navigation.NavRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val MAX_POINT_DIGITS = 2

class GameCalculatorViewModel(
    private val gameId: Int,
    private val navController: NavController,
    private val tresetaDatabase: TresetaDatabase,
) : ViewModel() {

    private val selectedTeam: MutableStateFlow<Team> = MutableStateFlow(Team.NONE)
    private val firstTeamCalls: MutableStateFlow<List<Call>> = MutableStateFlow(emptyList())
    private val secondTeamCalls: MutableStateFlow<List<Call>> = MutableStateFlow(emptyList())
    private val firstTeamPoints: MutableStateFlow<Int> = MutableStateFlow(0)
    private val secondTeamPoints: MutableStateFlow<Int> = MutableStateFlow(0)

    internal val viewState: StateFlow<GameCalculatorViewState> = combine(
        selectedTeam,
        firstTeamPoints,
        secondTeamPoints,
        firstTeamCalls,
        secondTeamCalls,
    ) { selection: Team, firstPoints: Int?, secondPoints: Int, firstCalls, secondCalls ->
        GameCalculatorViewState(
            firstTeamScore = calculatePointsPlusCalls(Team.FIRST),
            secondTeamScore = calculatePointsPlusCalls(Team.SECOND),
            selectedTeam = selection,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        GameCalculatorViewState()
    )

    internal fun onInteraction(interaction: GameCalculatorInteraction) {
        when (interaction) {
            is GameCalculatorInteraction.TapOnCallButton -> addCallToSelectedTeam(interaction.call)

            GameCalculatorInteraction.TapOnDeleteButton -> {
                firstTeamPoints.value = 0
                secondTeamPoints.value = 0
                firstTeamCalls.value = listOf()
                secondTeamCalls.value = listOf()
            }

            is GameCalculatorInteraction.TapOnNumberButton -> {
                parseInputForSelectedTeam(interaction.input)
            }

            GameCalculatorInteraction.TapOnSaveButton -> {
                viewModelScope.launch {
                    tresetaDatabase.roundDao().insertRound(
                        listOf(
                            RoundEntity(
                                id = 0,
                                gameId = gameId,
                                firstTeamPoints = calculatePointsPlusCalls(Team.FIRST),
                                secondTeamPoints = calculatePointsPlusCalls(Team.SECOND),
                            )
                        )
                    )
                }
                navController.popBackStack()
            }

            is GameCalculatorInteraction.TapOnTeamCard -> {
                selectedTeam.value = when (selectedTeam.value) {
                    interaction.team -> Team.NONE
                    else -> interaction.team
                }
            }

            GameCalculatorInteraction.TapOnBackButton -> navController.popBackStack()
        }
    }

    private fun calculatePointsPlusCalls(team: Team) = when (team) {
        Team.FIRST -> firstTeamPoints.value + firstTeamCalls.value.sumOf { it.value }
        Team.SECOND -> secondTeamPoints.value + secondTeamCalls.value.sumOf { it.value }
        else -> 0
    }

    private fun addCallToSelectedTeam(call: Call) {
        when (selectedTeam.value) {
            Team.FIRST -> firstTeamCalls.value += call
            Team.SECOND -> secondTeamCalls.value += call
            else -> Unit
        }
    }

    private fun parseInputForSelectedTeam(input: Int) {
        when (selectedTeam.value) {
            Team.FIRST -> {
                firstTeamPoints.value =
                    (firstTeamPoints.value.toString() + input.toString()).parseTeamPoints()
                secondTeamPoints.value = 11 - firstTeamPoints.value
            }

            Team.SECOND -> {
                secondTeamPoints.value =
                    (secondTeamPoints.value.toString() + input.toString()).parseTeamPoints()
                firstTeamPoints.value = 11 - secondTeamPoints.value
            }

            Team.NONE -> Unit
        }
    }

    private fun String.parseTeamPoints() =
        this.filter { it.isDigit() }.take(MAX_POINT_DIGITS).removePrefix("0").toInt()
            .coerceAtMost(11)

}