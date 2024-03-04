package com.premelc.tresetacounter.domain.treseta.tresetaGame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.premelc.tresetacounter.R
import com.premelc.tresetacounter.navigation.NavRoutes
import com.premelc.tresetacounter.ui.theme.Typography
import com.premelc.tresetacounter.uiComponents.FullActionToolbar
import com.premelc.tresetacounter.utils.Team
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun TresetaGameScreen(navController: NavController, navigate: (String) -> Unit) {
    val viewModel: TresetaGameViewModel = koinViewModel { parametersOf(navController) }
    val viewState = viewModel.viewState.collectAsStateWithLifecycle().value
    if (viewState is TresetaGameViewState.GameReady) {
        TresetaGameContent(viewState, viewModel::onInteraction, navigate)
    }
}

@Composable
internal fun TresetaGameContent(
    viewState: TresetaGameViewState.GameReady,
    onInteraction: (TresetaGameInteraction) -> Unit,
    navigate: (String) -> Unit
) {
    FullActionToolbar(
        title = stringResource(R.string.treseta_game_title),
        leftAction = {
            if (viewState.showHistoryButton) {
                Icon(
                    modifier = Modifier.clickable {
                        navigate(NavRoutes.TresetaGameHistory.route)
                        onInteraction(TresetaGameInteraction.TapOnHistoryButton)
                    },
                    painter = painterResource(R.drawable.history),
                    contentDescription = null,
                )
            }
        },
        rightAction = {
            Icon(
                modifier = Modifier.clickable { onInteraction(TresetaGameInteraction.TapOnMenuButton) },
                painter = painterResource(org.koin.android.R.drawable.abc_ic_menu_overflow_material),
                contentDescription = null,
            )
        },
    ) {
        Column {
            PointListColumn(viewState, navigate)
            CurrentScoreContent(viewState)
            Button(
                modifier = Modifier
                    .padding(20.dp)
                    .height(64.dp)
                    .fillMaxWidth(),
                onClick = {
                    navigate(NavRoutes.TresetaGameCalculator.route.plus("/${viewState.currentSetId}"))
                    onInteraction(TresetaGameInteraction.TapOnNewRound)
                },
            ) {
                Text(text = stringResource(R.string.game_add_new_round_button_label))
            }
        }
    }
    if (viewState.showSetFinishedModal) {
        SetFinishedDialog(
            winningTeam = viewState.winningTeam,
            onInteraction = onInteraction,
        )
    }
}

@Composable
private fun CurrentScoreContent(viewState: TresetaGameViewState.GameReady) {
    Column {
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
    }
}

@Composable
private fun SetFinishedDialog(
    winningTeam: Team,
    onInteraction: (TresetaGameInteraction) -> Unit
) {
    Dialog(
        onDismissRequest = { /*TODO*/ },
        properties = DialogProperties(
            dismissOnBackPress = false,
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
                text = stringResource(
                    if (winningTeam == Team.FIRST) {
                        R.string.game_set_finished_modal_text_first_team
                    } else {
                        R.string.game_set_finished_modal_text_second_team
                    }
                )
            )
            Button(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .height(60.dp)
                    .fillMaxWidth(),
                onClick = {
                    onInteraction(TresetaGameInteraction.TapOnSetFinishedModalConfirm)
                },
            ) {
                Text(stringResource(R.string.game_set_finished_modal_positive))
            }
        }
    }
}

@Composable
internal fun ColumnScope.PointListColumn(
    viewState: TresetaGameViewState.GameReady,
    navigate: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        TotalScoreContent(viewState)
        if (viewState.rounds.isEmpty()) {
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.game_no_rounds_yet_label),
                style = Typography.body1,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
            )
        } else {
            RoundsListContent(viewState, navigate)
        }
        Spacer(Modifier)
    }
}

@Composable
private fun ColumnScope.RoundsListContent(
    viewState: TresetaGameViewState.GameReady,
    navigate: (String) -> Unit,
) {
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
                            navigate(NavRoutes.TresetaRoundEdit.route.plus("/${it.id}"))
                        },
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = it.firstTeamPoints.toString(),
                        modifier = Modifier.padding(vertical = 10.dp),
                        style = Typography.body1
                    )
                    Text(
                        text = it.secondTeamPoints.toString(),
                        modifier = Modifier.padding(vertical = 10.dp),
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

@Composable
private fun TotalScoreContent(viewState: TresetaGameViewState.GameReady) {
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
}
