package com.premelc.tresetacounter.domain.gameCalculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.premelc.tresetacounter.R
import com.premelc.tresetacounter.domain.gameCalculator.GameCalculatorInteraction.TapOnCallButton
import com.premelc.tresetacounter.domain.gameCalculator.GameCalculatorInteraction.TapOnTeamCard
import com.premelc.tresetacounter.uiComponents.BuiltInNumPad
import com.premelc.tresetacounter.uiComponents.Calls
import com.premelc.tresetacounter.uiComponents.NumPadInteraction
import com.premelc.tresetacounter.uiComponents.RemovableCallsList
import com.premelc.tresetacounter.uiComponents.TeamPointCard
import com.premelc.tresetacounter.uiComponents.TresetaToolbarScaffold
import com.premelc.tresetacounter.uiComponents.animatePlacement
import com.premelc.tresetacounter.utils.Team
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun GameCalculatorScreen(
    navController: NavController,
    setId: Int,
) {
    val viewModel: GameCalculatorViewModel = koinViewModel { parametersOf(navController, setId) }
    val viewState = viewModel.viewState.collectAsStateWithLifecycle().value
    GameCalculatorContent(viewState, viewModel::onInteraction , viewModel::onNumPadInteraction)
}

@Composable
private fun GameCalculatorContent(
    viewState: GameCalculatorViewState,
    onInteraction: (GameCalculatorInteraction) -> Unit,
    onNumPadInteraction: (NumPadInteraction) -> Unit,
) {
    TresetaToolbarScaffold(backAction = { onInteraction(GameCalculatorInteraction.TapOnBackButton) }) {
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
                            GameCalculatorInteraction.TapOnRemovablePill(
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
                            GameCalculatorInteraction.TapOnRemovablePill(
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
}
