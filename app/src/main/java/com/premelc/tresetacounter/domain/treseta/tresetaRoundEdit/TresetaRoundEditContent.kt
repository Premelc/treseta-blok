package com.premelc.tresetacounter.domain.treseta.tresetaRoundEdit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.premelc.tresetacounter.R
import com.premelc.tresetacounter.uiComponents.BuiltInNumPad
import com.premelc.tresetacounter.uiComponents.Calls
import com.premelc.tresetacounter.uiComponents.CallsList
import com.premelc.tresetacounter.uiComponents.NumPadInteraction
import com.premelc.tresetacounter.uiComponents.RemovableCallsList
import com.premelc.tresetacounter.uiComponents.TeamPointCard
import com.premelc.tresetacounter.uiComponents.FullActionToolbar
import com.premelc.tresetacounter.uiComponents.animatePlacement
import com.premelc.tresetacounter.utils.Team
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private const val ROTATION_DEGREES = 90f

@Composable
internal fun TresetaRoundEditScreen(
    navController: NavController,
    roundId: Int,
) {
    val viewModel: TresetaRoundEditViewModel = koinViewModel { parametersOf(navController, roundId) }
    val viewState = viewModel.viewState.collectAsStateWithLifecycle().value
    RoundEditContent(viewState, viewModel::onInteraction, viewModel::onNumPadInteraction)
}

@Composable
private fun RoundEditContent(
    viewState: RoundEditViewState,
    onInteraction: (TresetaRoundEditInteraction) -> Unit,
    onNumPadInteraction: (NumPadInteraction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage = stringResource(R.string.treseta_calls_snackbar)
    val snackbarAction = stringResource(R.string.treseta_calls_snackbar_action)
    FullActionToolbar(
        snackbarHostState = {
            SnackbarHost(snackbarHostState)
        },
        leftAction = {
            Icon(
                modifier = Modifier.clickable { onInteraction(TresetaRoundEditInteraction.TapOnBackButton) },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null
            )
        },
        rightAction = {
            Icon(
                modifier = Modifier
                    .clickable { onInteraction(TresetaRoundEditInteraction.TapOnDeleteRound) }
                    .size(24.dp),
                painter = painterResource(R.drawable.trash),
                contentDescription = null
            )
        },
        title = stringResource(R.string.edit_round_toolbar_title)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OldRoundDataContent(roundData = viewState.oldRoundData)
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Icon(
                    modifier = Modifier
                        .rotate(ROTATION_DEGREES),
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
                Icon(
                    modifier = Modifier
                        .rotate(ROTATION_DEGREES),
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
            }
            NewRoundDataContent(viewState = viewState, onInteraction = onInteraction)
            Spacer(modifier = Modifier.weight(1f))
            BuiltInNumPad(
                onInteraction = onNumPadInteraction,
                isSaveButtonEnabled = viewState.isSaveButtonEnabled,
            )
        }
    }
    if (viewState.showDeleteRoundDialog) {
        DeleteRoundDialog(onInteraction)
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
                        onInteraction(TresetaRoundEditInteraction.DismissCallsSnackbar)
                    }
                }
            }
        }
    }
}

@Composable
private fun NewRoundDataContent(
    viewState: RoundEditViewState,
    onInteraction: (TresetaRoundEditInteraction) -> Unit,
) {
    Column {
        Row(Modifier.padding(horizontal = 20.dp)) {
            RemovableCallsList(
                horizontalArrangement = Arrangement.Start,
                calls = viewState.newRoundData.firstTeamCalls,
                onClick = { index ->
                    onInteraction(
                        TresetaRoundEditInteraction.TapOnRemovablePill(
                            index = index,
                            team = Team.FIRST
                        )
                    )
                },
            )
            RemovableCallsList(
                horizontalArrangement = Arrangement.End,
                calls = viewState.newRoundData.secondTeamCalls,
                onClick = { index ->
                    onInteraction(
                        TresetaRoundEditInteraction.TapOnRemovablePill(
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
                    pointValue = viewState.newRoundData.firstTeamScore,
                    callsValue = viewState.newRoundData.firstTeamCalls.sumOf { it.value },
                    isSelected = viewState.selectedTeam == Team.FIRST,
                    onClick = {
                        onInteraction(TresetaRoundEditInteraction.TapOnTeamCard(Team.FIRST))
                    },
                )
                TeamPointCard(
                    team = stringResource(R.string.game_calculator_second_team_title),
                    pointValue = viewState.newRoundData.secondTeamScore,
                    callsValue = viewState.newRoundData.secondTeamCalls.sumOf { it.value },
                    isSelected = viewState.selectedTeam == Team.SECOND,
                    onClick = {
                        onInteraction(TresetaRoundEditInteraction.TapOnTeamCard(Team.SECOND))
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
                    onInteraction(TresetaRoundEditInteraction.TapOnCallButton(call))
                },
            )
        }
    }
}

@Composable
private fun OldRoundDataContent(
    roundData: TresetaRoundData,
) {
    Column {
        Row(Modifier.padding(horizontal = 20.dp)) {
            CallsList(
                horizontalArrangement = Arrangement.Start,
                calls = roundData.firstTeamCalls,
            )
            CallsList(
                horizontalArrangement = Arrangement.End,
                calls = roundData.secondTeamCalls,
            )
        }
        Column(modifier = Modifier.animatePlacement()) {
            Row(Modifier.padding(20.dp)) {
                TeamPointCard(
                    team = stringResource(R.string.game_calculator_first_team_title),
                    pointValue = roundData.firstTeamScore,
                    callsValue = roundData.firstTeamCalls.sumOf { it.value },
                    isEnabled = false,
                    isSelected = false,
                )
                TeamPointCard(
                    team = stringResource(R.string.game_calculator_second_team_title),
                    pointValue = roundData.secondTeamScore,
                    callsValue = roundData.secondTeamCalls.sumOf { it.value },
                    isEnabled = false,
                    isSelected = false,
                )
            }
        }
    }
}

@Composable
private fun DeleteRoundDialog(
    onInteraction: (TresetaRoundEditInteraction) -> Unit
) {
    Dialog(
        onDismissRequest = { /*TODO*/ },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        )
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(MaterialTheme.colors.background)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                text = stringResource(id = R.string.edit_round_delete_dialog_title)
            )
            Button(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .height(60.dp)
                    .fillMaxWidth(),
                onClick = {
                    onInteraction(TresetaRoundEditInteraction.TapOnDeleteRoundDialogPositive)
                },
            ) {
                Text(text = stringResource(R.string.edit_round_delete_dialog_button_positive))
            }
            Button(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .height(60.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.background,
                ),
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.onBackground),
                onClick = {
                    onInteraction(TresetaRoundEditInteraction.TapOnDeleteRoundDialogNegative)
                },
            ) {
                Text(text = stringResource(R.string.edit_round_delete_dialog_button_negative))
            }
        }
    }
}
