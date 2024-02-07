package com.premelc.tresetacounter.domain.briscola.briscolaGame

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premelc.tresetacounter.R
import com.premelc.tresetacounter.navigation.NavRoutes
import com.premelc.tresetacounter.ui.theme.Typography
import com.premelc.tresetacounter.uiComponents.TresetaFullActionToolbar
import com.premelc.tresetacounter.utils.Team
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BriscolaGameScreen(navigate: (String) -> Unit) {
    val viewModel: BriscolaGameViewModel = koinViewModel()
    val viewState = viewModel.viewState.collectAsStateWithLifecycle().value
    if (viewState is BriscolaGameViewState.GameReady) {
        BriscolaGameContent(viewState, viewModel::onInteraction, navigate)
    }
}

@Composable
internal fun BriscolaGameContent(
    viewState: BriscolaGameViewState.GameReady,
    onInteraction: (BriscolaGameInteraction) -> Unit,
    navigate: (String) -> Unit
) {
    TresetaFullActionToolbar(
        title = stringResource(R.string.briscola_game_title),
        leftAction = {
            if (viewState.showHistoryButton) {
                Icon(
                    modifier = Modifier.clickable {
                        navigate(NavRoutes.GameHistory.route)
                        onInteraction(BriscolaGameInteraction.TapOnHistoryButton)
                    },
                    painter = painterResource(R.drawable.history),
                    contentDescription = null,
                )
            }
        },
        rightAction = {
            Icon(
                modifier = Modifier.clickable {
                    navigate(NavRoutes.MainMenu.route)
                    onInteraction(BriscolaGameInteraction.TapOnMenuButton)
                },
                painter = painterResource(org.koin.android.R.drawable.abc_ic_menu_overflow_material),
                contentDescription = null,
            )
        },
    ) {
        Column {
            PointListColumn(viewState, navigate)
            Divider(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colors.onBackground
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = viewState.rounds.sumOf { it.firstTeamPoints }.toString(),
                    style = Typography.h6,
                    textDecoration = if (viewState.winningTeam == Team.FIRST) TextDecoration.Underline else null
                )
                Text(
                    text = viewState.rounds.sumOf { it.secondTeamPoints }.toString(),
                    style = Typography.h6,
                    textDecoration = if (viewState.winningTeam == Team.SECOND) TextDecoration.Underline else null
                )
            }
            Divider(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colors.onBackground
            )
            Button(
                modifier = Modifier
                    .padding(20.dp)
                    .height(64.dp)
                    .fillMaxWidth(),
                onClick = {
                    navigate(NavRoutes.GameCalculator.route.plus("/${viewState.currentSetId}"))
                    onInteraction(BriscolaGameInteraction.TapOnNewRound)
                },
            ) {
                Text(text = stringResource(R.string.game_add_new_round_button_label))
            }
        }
    }
}

@Composable
internal fun ColumnScope.PointListColumn(
    viewState: BriscolaGameViewState.GameReady,
    navigate: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = viewState.firstTeamScore.toString(), style = Typography.h6)
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = ":",
                    style = Typography.h6
                )
                Text(text = viewState.secondTeamScore.toString(), style = Typography.h6)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = stringResource(R.string.game_first_team_title),
                    style = Typography.h6,
                )
                Text(
                    text = stringResource(R.string.game_second_team_title),
                    style = Typography.h6,
                )
            }
            Divider(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colors.onBackground
            )
        }
        if (viewState.rounds.isEmpty()) {
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.game_no_rounds_yet_label),
                style = Typography.body1,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
            )
        } else {
            Box(Modifier.weight(1f)) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = painterResource(id = R.drawable.treseta_cards),
                    contentDescription = null,
                    alpha = 0.1f
                )
                LazyColumn {
                    items(viewState.rounds) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navigate(NavRoutes.RoundEdit.route.plus("/${it.id}"))
                                },
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = it.firstTeamPoints.toString(),
                                modifier = Modifier.padding(vertical = 8.dp),
                                style = Typography.body1
                            )
                            Text(
                                text = it.secondTeamPoints.toString(),
                                modifier = Modifier.padding(vertical = 8.dp),
                                style = Typography.body1
                            )
                        }
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
        Spacer(Modifier)
    }
}
