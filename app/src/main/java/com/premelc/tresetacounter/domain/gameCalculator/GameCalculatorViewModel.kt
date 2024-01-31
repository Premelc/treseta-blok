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

    private val selectedTeamFlow: MutableStateFlow<Team> = MutableStateFlow(Team.FIRST)
    private val firstTeamCallsFlow: MutableStateFlow<List<Call>> = MutableStateFlow(listOf())
    private val secondTeamCallsFlow: MutableStateFlow<List<Call>> = MutableStateFlow(listOf())
    private val firstTeamPointsFlow: MutableStateFlow<Int> = MutableStateFlow(0)
    private val secondTeamPointsFlow: MutableStateFlow<Int> = MutableStateFlow(0)

    internal val viewState: StateFlow<GameCalculatorViewState> = combine(
        selectedTeamFlow,
        firstTeamPointsFlow,
        secondTeamPointsFlow,
        firstTeamCallsFlow,
        secondTeamCallsFlow,
    ) { selection: Team, firstTeamPoints: Int, secondTeamPoints: Int, firstTeamCalls, secondTeamCalls ->
        GameCalculatorViewState(
            firstTeamScore = firstTeamPoints,
            secondTeamScore = secondTeamPoints,
            firstTeamCalls = firstTeamCalls,
            secondTeamCalls = secondTeamCalls,
            selectedTeam = selection,
            isSaveButtonEnabled = firstTeamPoints > 0 || secondTeamPoints > 0,
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
                firstTeamPointsFlow.value = 0
                secondTeamPointsFlow.value = 0
                firstTeamCallsFlow.value = mutableListOf()
                secondTeamCallsFlow.value = mutableListOf()
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
                        firstTeamCalls = firstTeamCallsFlow.value,
                        secondTeamCalls = secondTeamCallsFlow.value,
                    )
                }
                navController.popBackStack()
            }

            is GameCalculatorInteraction.TapOnTeamCard -> {
                selectedTeamFlow.value = when (selectedTeamFlow.value) {
                    interaction.team -> Team.NONE
                    else -> interaction.team
                }
            }

            GameCalculatorInteraction.TapOnBackButton -> navController.popBackStack()
            is GameCalculatorInteraction.TapOnRemovablePill -> {
                if (interaction.team == Team.FIRST) {
                    firstTeamCallsFlow.value =
                        firstTeamCallsFlow.value.filterIndexed { index, _ -> index != interaction.index }
                } else {
                    secondTeamCallsFlow.value =
                        secondTeamCallsFlow.value.filterIndexed { index, _ -> index != interaction.index }
                }
            }
        }
    }

    private fun calculatePointsPlusCalls(team: Team) = when (team) {
        Team.FIRST -> firstTeamPointsFlow.value + firstTeamCallsFlow.value.sumOf { it.value }
        Team.SECOND -> secondTeamPointsFlow.value + secondTeamCallsFlow.value.sumOf { it.value }
        else -> 0
    }

    private fun addCallToSelectedTeam(call: Call) {
        when (selectedTeamFlow.value) {
            Team.FIRST -> firstTeamCallsFlow.value += call
            Team.SECOND -> secondTeamCallsFlow.value += call
            else -> Unit
        }
    }

    private fun parseInputForSelectedTeam(input: Int) {
        when (selectedTeamFlow.value) {
            Team.FIRST -> {
                firstTeamPointsFlow.value =
                    (firstTeamPointsFlow.value.toString() + input.toString()).parseTeamPoints()
                secondTeamPointsFlow.value = 11 - firstTeamPointsFlow.value
            }

            Team.SECOND -> {
                secondTeamPointsFlow.value =
                    (secondTeamPointsFlow.value.toString() + input.toString()).parseTeamPoints()
                firstTeamPointsFlow.value = 11 - secondTeamPointsFlow.value
            }

            Team.NONE -> Unit
        }
    }

    private fun String.parseTeamPoints() =
        this.filter { it.isDigit() }.take(MAX_POINT_DIGITS).removePrefix("0").toInt()
            .coerceAtMost(11)
}