package com.premelc.tresetacounter.domain.briscola.briscolaCalculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.premelc.tresetacounter.service.BriscolaService
import com.premelc.tresetacounter.uiComponents.NumPadInteraction
import com.premelc.tresetacounter.utils.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val MAX_POINT_DIGITS = 3

class BriscolaCalculatorViewModel(
    private val setId: Int,
    private val briscolaService: BriscolaService,
    private val navController: NavController,
) : ViewModel() {

    private val selectedTeamFlow: MutableStateFlow<Team> = MutableStateFlow(Team.FIRST)
    private val firstTeamPointsFlow: MutableStateFlow<Int> = MutableStateFlow(0)
    private val secondTeamPointsFlow: MutableStateFlow<Int> = MutableStateFlow(0)

    internal val viewState: StateFlow<BriscolaCalculatorViewState> = combine(
        selectedTeamFlow,
        firstTeamPointsFlow,
        secondTeamPointsFlow,
    ) { selection, firstTeamPoints, secondTeamPoints ->
        BriscolaCalculatorViewState(
            firstTeamScore = firstTeamPoints,
            secondTeamScore = secondTeamPoints,
            selectedTeam = selection,
            isSaveButtonEnabled = firstTeamPoints > 0 || secondTeamPoints > 0,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        BriscolaCalculatorViewState()
    )

    internal fun onInteraction(interaction: BriscolaCalculatorInteraction) {
        when (interaction) {

            is BriscolaCalculatorInteraction.TapOnTeamCard -> {
                selectedTeamFlow.value = when (selectedTeamFlow.value) {
                    interaction.team -> Team.NONE
                    else -> interaction.team
                }
            }

            BriscolaCalculatorInteraction.TapOnBackButton -> navController.popBackStack()
        }
    }

    fun onNumPadInteraction(interaction: NumPadInteraction) {
        when (interaction) {
            NumPadInteraction.TapOnDeleteButton -> {
                firstTeamPointsFlow.value = 0
                secondTeamPointsFlow.value = 0
            }

            is NumPadInteraction.TapOnNumberButton -> {
                parseInputForSelectedTeam(interaction.input)
            }

            NumPadInteraction.TapOnSaveButton -> {
                viewModelScope.launch {
                    briscolaService.insertRound(
                        setId = setId,
                        firstTeamPoints = firstTeamPointsFlow.value,
                        secondTeamPoints = secondTeamPointsFlow.value,
                    )
                }
                navController.popBackStack()
            }
        }
    }

    private fun parseInputForSelectedTeam(input: Int) {
        when (selectedTeamFlow.value) {
            Team.FIRST -> {
                firstTeamPointsFlow.value =
                    (firstTeamPointsFlow.value.toString() + input.toString()).parseTeamPoints()
                secondTeamPointsFlow.value = 120 - firstTeamPointsFlow.value
            }

            Team.SECOND -> {
                secondTeamPointsFlow.value =
                    (secondTeamPointsFlow.value.toString() + input.toString()).parseTeamPoints()
                firstTeamPointsFlow.value = 120 - secondTeamPointsFlow.value
            }

            Team.NONE -> Unit
        }
    }

    private fun String.parseTeamPoints() =
        this.filter { it.isDigit() }.take(MAX_POINT_DIGITS).removePrefix("0").toInt()
            .coerceAtMost(120)
}