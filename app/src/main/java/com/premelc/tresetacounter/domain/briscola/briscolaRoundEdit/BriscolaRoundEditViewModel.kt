package com.premelc.tresetacounter.domain.briscola.briscolaRoundEdit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.premelc.tresetacounter.service.BriscolaService
import com.premelc.tresetacounter.uiComponents.NumPadInteraction
import com.premelc.tresetacounter.utils.Team
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val MAX_POINT_DIGITS = 3

class BriscolaRoundEditViewModel(
    private val roundId: Int,
    private val briscolaService: BriscolaService,
    private val navController: NavController,
) : ViewModel() {

    private val oldRoundData = viewModelScope.async(start = CoroutineStart.LAZY) {
        briscolaService.getSingleRound(roundId)
    }

    private val selectedTeamFlow: MutableStateFlow<Team> = MutableStateFlow(Team.FIRST)
    private val firstTeamPointsFlow: MutableStateFlow<Int> = MutableStateFlow(0)
    private val secondTeamPointsFlow: MutableStateFlow<Int> = MutableStateFlow(0)
    private val deleteRoundDialogFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    internal val viewState: StateFlow<BriscolaRoundEditViewState> = combine(
        selectedTeamFlow,
        firstTeamPointsFlow,
        secondTeamPointsFlow,
        deleteRoundDialogFlow,
    ) { selection, firstTeamPoints, secondTeamPoints, showDeleteRoundDialog ->
        BriscolaRoundEditViewState(
            oldRoundData = BriscolaRoundData(
                firstTeamScore = oldRoundData.await().firstTeamPointsCollected,
                secondTeamScore = oldRoundData.await().secondTeamPointsCollected,
            ),
            newRoundData = BriscolaRoundData(
                firstTeamScore = firstTeamPoints,
                secondTeamScore = secondTeamPoints,
            ),
            selectedTeam = selection,
            isSaveButtonEnabled = firstTeamPoints > 0 || secondTeamPoints > 0,
            showDeleteRoundDialog = showDeleteRoundDialog
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        BriscolaRoundEditViewState()
    )

    internal fun onInteraction(interaction: BriscolaRoundEditInteraction) {
        when (interaction) {

            is BriscolaRoundEditInteraction.TapOnTeamCard -> {
                selectedTeamFlow.value = when (selectedTeamFlow.value) {
                    interaction.team -> Team.NONE
                    else -> interaction.team
                }
            }

            BriscolaRoundEditInteraction.TapOnBackButton -> navController.popBackStack()

            BriscolaRoundEditInteraction.TapOnDeleteRound -> deleteRoundDialogFlow.value = true
            BriscolaRoundEditInteraction.TapOnDeleteRoundDialogNegative -> {
                deleteRoundDialogFlow.value = false
            }

            BriscolaRoundEditInteraction.TapOnDeleteRoundDialogPositive -> {
                deleteRoundDialogFlow.value = false
                viewModelScope.launch {
                    briscolaService.deleteSingleRound(roundId)
                    navController.popBackStack()
                }
            }
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
                    briscolaService.editRound(
                        roundId = roundId,
                        setId = oldRoundData.await().setId,
                        firstTeamPoints = firstTeamPointsFlow.value,
                        secondTeamPoints = secondTeamPointsFlow.value,
                        timestamp = oldRoundData.await().timestamp,
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