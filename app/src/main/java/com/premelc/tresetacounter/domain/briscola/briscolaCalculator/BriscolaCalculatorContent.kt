package com.premelc.tresetacounter.domain.briscola.briscolaCalculator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.premelc.tresetacounter.R
import com.premelc.tresetacounter.uiComponents.BuiltInNumPad
import com.premelc.tresetacounter.uiComponents.NumPadInteraction
import com.premelc.tresetacounter.uiComponents.TeamPointCard
import com.premelc.tresetacounter.uiComponents.ToolbarScaffold
import com.premelc.tresetacounter.uiComponents.animatePlacement
import com.premelc.tresetacounter.utils.Team
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun BriscolaGameCalculatorScreen(
    navController: NavController,
    setId: Int,
) {
    val viewModel: BriscolaCalculatorViewModel = koinViewModel { parametersOf(navController, setId) }
    val viewState = viewModel.viewState.collectAsStateWithLifecycle().value
    GameCalculatorContent(viewState, viewModel::onInteraction , viewModel::onNumPadInteraction)
}

@Composable
private fun GameCalculatorContent(
    viewState: BriscolaCalculatorViewState,
    onInteraction: (BriscolaCalculatorInteraction) -> Unit,
    onNumPadInteraction: (NumPadInteraction) -> Unit,
) {
    ToolbarScaffold(backAction = { onInteraction(BriscolaCalculatorInteraction.TapOnBackButton) }) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(modifier = Modifier.animatePlacement()) {
                Row(Modifier.padding(20.dp)) {
                    TeamPointCard(
                        team = stringResource(R.string.game_calculator_first_team_title),
                        pointValue = viewState.firstTeamScore,
                        isSelected = viewState.selectedTeam == Team.FIRST,
                        onClick = {
                            onInteraction(
                                BriscolaCalculatorInteraction.TapOnTeamCard(Team.FIRST)
                            )
                        },
                    )
                    TeamPointCard(
                        team = stringResource(R.string.game_calculator_second_team_title),
                        pointValue = viewState.secondTeamScore,
                        isSelected = viewState.selectedTeam == Team.SECOND,
                        onClick = {
                            onInteraction(BriscolaCalculatorInteraction.TapOnTeamCard(Team.SECOND))
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            BuiltInNumPad(
                onInteraction = onNumPadInteraction,
                isSaveButtonEnabled = viewState.isSaveButtonEnabled,
            )
        }
    }
}
