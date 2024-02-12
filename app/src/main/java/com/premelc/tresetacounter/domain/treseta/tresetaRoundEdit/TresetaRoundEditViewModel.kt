package com.premelc.tresetacounter.domain.treseta.tresetaRoundEdit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.premelc.tresetacounter.service.TresetaService
import com.premelc.tresetacounter.uiComponents.NumPadInteraction
import com.premelc.tresetacounter.utils.Call
import com.premelc.tresetacounter.utils.Team
import com.premelc.tresetacounter.utils.combine
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val MAX_POINT_DIGITS = 2

class TresetaRoundEditViewModel(
    private val roundId: Int,
    private val tresetaService: TresetaService,
    private val navController: NavController,
) : ViewModel() {

    private val oldRoundData = viewModelScope.async(start = CoroutineStart.LAZY) {
        tresetaService.getSingleRound(roundId)
    }

    private var interactionsEnabled = true
    private val selectedTeamFlow: MutableStateFlow<Team> = MutableStateFlow(Team.FIRST)
    private val firstTeamCallsFlow: MutableStateFlow<List<Call>> = MutableStateFlow(listOf())
    private val secondTeamCallsFlow: MutableStateFlow<List<Call>> = MutableStateFlow(listOf())
    private val firstTeamPointsFlow: MutableStateFlow<Int> = MutableStateFlow(0)
    private val secondTeamPointsFlow: MutableStateFlow<Int> = MutableStateFlow(0)
    private val deleteRoundDialogFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val showCallsSnackarFlow = MutableStateFlow(false)

    init {
        interactionsEnabled = true
    }

    internal val viewState: StateFlow<RoundEditViewState> = combine(
        selectedTeamFlow,
        firstTeamPointsFlow,
        secondTeamPointsFlow,
        firstTeamCallsFlow,
        secondTeamCallsFlow,
        deleteRoundDialogFlow,
        showCallsSnackarFlow,
    ) { selection, firstTeamPoints, secondTeamPoints, firstTeamCalls, secondTeamCalls, showDeleteRoundDialog, showCallsSnackbar ->
        RoundEditViewState(
            oldRoundData = TresetaRoundData(
                firstTeamScore = oldRoundData.await().firstTeamPointsNoCalls,
                firstTeamCalls = oldRoundData.await().firstTeamCalls,
                secondTeamScore = oldRoundData.await().secondTeamPointsNoCalls,
                secondTeamCalls = oldRoundData.await().secondTeamCalls,
            ),
            newRoundData = TresetaRoundData(
                firstTeamScore = firstTeamPoints,
                firstTeamCalls = firstTeamCalls,
                secondTeamScore = secondTeamPoints,
                secondTeamCalls = secondTeamCalls,
            ),
            selectedTeam = selection,
            isSaveButtonEnabled = firstTeamPoints > 0 || secondTeamPoints > 0,
            showDeleteRoundDialog = showDeleteRoundDialog,
            showCallsSnackbar = showCallsSnackbar,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        RoundEditViewState()
    )

    internal fun onInteraction(interaction: TresetaRoundEditInteraction) {
        when (interaction) {
            is TresetaRoundEditInteraction.TapOnCallButton -> addCallToSelectedTeam(interaction.call)

            is TresetaRoundEditInteraction.TapOnTeamCard -> {
                selectedTeamFlow.value = when (selectedTeamFlow.value) {
                    interaction.team -> Team.NONE
                    else -> interaction.team
                }
            }

            TresetaRoundEditInteraction.TapOnBackButton -> navController.popBackStack()
            is TresetaRoundEditInteraction.TapOnRemovablePill -> {
                if (interaction.team == Team.FIRST) {
                    firstTeamCallsFlow.value =
                        firstTeamCallsFlow.value.filterIndexed { index, _ -> index != interaction.index }
                } else {
                    secondTeamCallsFlow.value =
                        secondTeamCallsFlow.value.filterIndexed { index, _ -> index != interaction.index }
                }
            }

            TresetaRoundEditInteraction.TapOnDeleteRound -> deleteRoundDialogFlow.value = true
            TresetaRoundEditInteraction.TapOnDeleteRoundDialogNegative -> {
                deleteRoundDialogFlow.value = false
            }

            TresetaRoundEditInteraction.TapOnDeleteRoundDialogPositive -> {
                deleteRoundDialogFlow.value = false
                viewModelScope.launch {
                    tresetaService.deleteSingleRound(roundId)
                    navController.popBackStack()
                }
            }
            TresetaRoundEditInteraction.DismissCallsSnackbar -> showCallsSnackarFlow.value = false
        }
    }

    fun onNumPadInteraction(interaction: NumPadInteraction) {
        when (interaction) {
            NumPadInteraction.TapOnDeleteButton -> {
                firstTeamPointsFlow.value = 0
                secondTeamPointsFlow.value = 0
                firstTeamCallsFlow.value = mutableListOf()
                secondTeamCallsFlow.value = mutableListOf()
            }

            is NumPadInteraction.TapOnNumberButton -> {
                parseInputForSelectedTeam(interaction.input)
            }

            NumPadInteraction.TapOnSaveButton -> {
                if (interactionsEnabled) {
                    interactionsEnabled = false
                    viewModelScope.launch {
                        tresetaService.editRound(
                            roundId = roundId,
                            setId = oldRoundData.await().setId,
                            firstTeamPoints = calculatePointsPlusCalls(Team.FIRST),
                            firstTeamPointsNoCalls = firstTeamPointsFlow.value,
                            secondTeamPoints = calculatePointsPlusCalls(Team.SECOND),
                            secondTeamPointsNoCalls = secondTeamPointsFlow.value,
                            timestamp = oldRoundData.await().timestamp,
                            firstTeamCalls = firstTeamCallsFlow.value,
                            secondTeamCalls = secondTeamCallsFlow.value
                        )
                    }
                    navController.popBackStack()
                }
            }
        }
    }

    private fun addCallToSelectedTeam(call: Call) {
        if (checkIfMaxCallsReached(call)) {
            when (selectedTeamFlow.value) {
                Team.FIRST -> firstTeamCallsFlow.value += call
                Team.SECOND -> secondTeamCallsFlow.value += call
                else -> Unit
            }
        } else {
            showCallsSnackarFlow.value = true
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

    private fun checkIfMaxCallsReached(call: Call): Boolean {
        val totalCalls = firstTeamCallsFlow.value + secondTeamCallsFlow.value
        return when (call) {
            Call.NAPOLITANA -> totalCalls.count { it == Call.NAPOLITANA } < 4
            Call.X3, Call.X4 -> totalCalls.count { it == Call.X3 } + totalCalls.count { it == Call.X4 } < 3
        }
    }

    private fun calculatePointsPlusCalls(team: Team) = when (team) {
        Team.FIRST -> firstTeamPointsFlow.value + firstTeamCallsFlow.value.sumOf { it.value }
        Team.SECOND -> secondTeamPointsFlow.value + secondTeamCallsFlow.value.sumOf { it.value }
        else -> 0
    }

    private fun String.parseTeamPoints() =
        this.filter { it.isDigit() }.take(MAX_POINT_DIGITS).removePrefix("0").toInt()
            .coerceAtMost(11)
}