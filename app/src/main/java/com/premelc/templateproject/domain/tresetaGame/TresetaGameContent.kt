package com.premelc.templateproject.domain.tresetaGame

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.premelc.templateproject.R
import com.premelc.templateproject.domain.gameCalculator.Team
import com.premelc.templateproject.ui.theme.Typography
import com.premelc.templateproject.uiComponents.TresetaGameScaffold
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun TresetaGameScreen(navController: NavController) {
    val viewModel: TresetaGameViewModel = koinViewModel { parametersOf(navController) }
    val viewState = viewModel.viewState.collectAsStateWithLifecycle().value
    if (viewState is TresetaGameViewState.GameReady) {
        TresetaGameContent(viewState, viewModel::onInteraction)
    }
}

@Composable
internal fun TresetaGameContent(
    viewState: TresetaGameViewState.GameReady,
    onInteraction: (TresetaGameInteraction) -> Unit,
) {
    TresetaGameScaffold(
        leftAction = {
            if (viewState.showHistoryButton) {
                Icon(
                    modifier = Modifier.clickable { onInteraction(TresetaGameInteraction.TapOnHistoryButton) },
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
            PointListColumn(viewState)
            Divider(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colors.onSurface
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
                color = MaterialTheme.colors.onSurface
            )
            Button(
                modifier = Modifier
                    .padding(20.dp)
                    .height(64.dp)
                    .fillMaxWidth(),
                onClick = { onInteraction(TresetaGameInteraction.TapOnNewRound) },
            ) {
                Text(text = "Dodaj novu partiju")
            }
        }
    }
}

@Composable
internal fun ColumnScope.PointListColumn(
    viewState: TresetaGameViewState.GameReady,
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
                Text(text = "MI", style = Typography.h6)
                Text(text = "VI", style = Typography.h6)
            }
            Divider(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colors.onSurface
            )
        }
        if (viewState.rounds.isEmpty()) {
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                textAlign = TextAlign.Center,
                text = "Nije odigrana nijedna partija, pritisnite gumb za dodavanje partije",
                style = Typography.body1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
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
                            modifier = Modifier.fillMaxWidth(),
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
