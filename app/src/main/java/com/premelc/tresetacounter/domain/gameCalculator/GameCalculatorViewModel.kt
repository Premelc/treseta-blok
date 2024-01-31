package com.premelc.tresetacounter.domain.gameCalculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.premelc.tresetacounter.service.TresetaService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val MAX_POINT_DIGITS = 2

class GameCalculatorViewModel(
    private val setId: Int,
    private val tresetaService: TresetaService,
    private val navController: NavController,
) : ViewModel() {

    private val selectedTeam: MutableStateFlow<Team> = MutableStateFlow(Team.FIRST)
    private val firstTeamCalls: MutableStateFlow<List<Call>> = MutableStateFlow(listOf())
    private val secondTeamCalls: MutableStateFlow<List<Call>> = MutableStateFlow(listOf())
    private val firstTeamPoints: MutableStateFlow<Int> = MutableStateFlow(0)
    private val secondTeamPoints: MutableStateFlow<Int> = MutableStateFlow(0)

    internal val viewState: StateFlow<GameCalculatorViewState> = combine(
        selectedTeam,
        firstTeamPoints,
        secondTeamPoints,
        firstTeamCalls,
        secondTeamCalls,
    ) { selection: Team, _: Int?, _: Int, firstTeamCalls, secondTeamCalls ->
        GameCalculatorViewState(
            firstTeamScore = calculatePointsPlusCalls(Team.FIRST),
            secondTeamScore = calculatePointsPlusCalls(Team.SECOND),
            firstTeamCalls = firstTeamCalls,
            secondTeamCalls = secondTeamCalls,
            selectedTeam = selection,
            isSaveButtonEnabled = firstTeamPoints.value > 0 || secondTeamPoints.value > 0,
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
                firstTeamCalls.value = mutableListOf()
                secondTeamCalls.value = mutableListOf()
            }

            is GameCalculatorInteraction.TapOnNumberButton -> {
                parseInputForSelectedTeam(interaction.input)
            }

            GameCalculatorInteraction.TapOnSaveButton -> {
                viewModelScope.launch {
                    tresetaService.insertRound(
                        setId = setId,
                        firstTeamPoints = calculatePointsPlusCalls(Team.FIRST),
                        secondTeamPoints = calculatePointsPlusCalls(Team.SECOND),
                        firstTeamCalls = firstTeamCalls.value,
                        secondTeamCalls = secondTeamCalls.value,
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
            is GameCalculatorInteraction.TapOnRemovablePill -> {
                if (interaction.team == Team.FIRST) {
                    firstTeamCalls.value =
                        firstTeamCalls.value.filterIndexed { index, _ -> index != interaction.index }
                } else {
                    secondTeamCalls.value =
                        secondTeamCalls.value.filterIndexed { index, _ -> index != interaction.index }
                }
            }
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