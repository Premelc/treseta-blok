package com.premelc.tresetacounter.domain.treseta.tresetaCalculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.premelc.tresetacounter.R
import com.premelc.tresetacounter.domain.treseta.tresetaCalculator.TresetaCalculatorInteraction.TapOnCallButton
import com.premelc.tresetacounter.domain.treseta.tresetaCalculator.TresetaCalculatorInteraction.TapOnTeamCard
import com.premelc.tresetacounter.uiComponents.BuiltInNumPad
import com.premelc.tresetacounter.uiComponents.Calls
import com.premelc.tresetacounter.uiComponents.NumPadInteraction
import com.premelc.tresetacounter.uiComponents.RemovableCallsList
import com.premelc.tresetacounter.uiComponents.TeamPointCard
import com.premelc.tresetacounter.uiComponents.ToolbarScaffold
import com.premelc.tresetacounter.uiComponents.animatePlacement
import com.premelc.tresetacounter.utils.Team
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun TresetaGameCalculatorScreen(
    navController: NavController,
    setId: Int,
) {
    val viewModel: TresetaCalculatorViewModel = koinViewModel { parametersOf(navController, setId) }
    val viewState = viewModel.viewState.collectAsStateWithLifecycle().value
    GameCalculatorContent(viewState, viewModel::onInteraction, viewModel::onNumPadInteraction)
}

@Composable
private fun GameCalculatorContent(
    viewState: TresetaCalculatorViewState,
    onInteraction: (TresetaCalculatorInteraction) -> Unit,
    onNumPadInteraction: (NumPadInteraction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage = stringResource(R.string.treseta_calls_snackbar)
    val snackbarAction = stringResource(R.string.treseta_calls_snackbar_action)
    ToolbarScaffold(
        backAction = { onInteraction(TresetaCalculatorInteraction.TapOnBackButton) },
        snackbarHostState = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(Modifier.padding(horizontal = 20.dp)) {
                RemovableCallsList(
                    horizontalArrangement = Arrangement.Start,
                    calls = viewState.firstTeamCalls,
                    onClick = { index ->
                        onInteraction(
                            TresetaCalculatorInteraction.TapOnRemovablePill(
                                index = index,
                                team = Team.FIRST
                            )
                        )
                    },
                )
                RemovableCallsList(
                    horizontalArrangement = Arrangement.End,
                    calls = viewState.secondTeamCalls,
                    onClick = { index ->
                        onInteraction(
                            TresetaCalculatorInteraction.TapOnRemovablePill(
                                index = index,
                                team = Team.SECOND
                            )
                        )
                    },
                )
            }
            Column(modifier = Modifier.animatePlacement()) {
                Row(Modifier.padding(20.dp)) {
                    TeamPointCard(
                        team = stringResource(R.string.game_calculator_first_team_title),
                        pointValue = viewState.firstTeamScore,
                        callsValue = viewState.firstTeamCalls.sumOf { it.value },
                        isSelected = viewState.selectedTeam == Team.FIRST,
                        onClick = {
                            onInteraction(TapOnTeamCard(Team.FIRST))
                        },
                    )
                    TeamPointCard(
                        team = stringResource(R.string.game_calculator_second_team_title),
                        pointValue = viewState.secondTeamScore,
                        callsValue = viewState.secondTeamCalls.sumOf { it.value },
                        isSelected = viewState.selectedTeam == Team.SECOND,
                        onClick = {
                            onInteraction(TapOnTeamCard(Team.SECOND))
                        },
                    )
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.game_calculator_calls_title),
                )
                Calls(
                    onInteraction = { call ->
                        onInteraction(TapOnCallButton(call))
                    },
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            BuiltInNumPad(
                onInteraction = onNumPadInteraction,
                isSaveButtonEnabled = viewState.isSaveButtonEnabled,
            )
        }
    }
    LaunchedEffect(viewState.showCallsSnackbar) {
        if (viewState.showCallsSnackbar) {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = snackbarMessage,
                    actionLabel = snackbarAction,
                    duration = SnackbarDuration.Short
                )
                when (result) {
                    SnackbarResult.Dismissed, SnackbarResult.ActionPerformed -> {
                        onInteraction(TresetaCalculatorInteraction.DismissCallsSnackbar)
                    }
                }
            }
        }
    }
}
