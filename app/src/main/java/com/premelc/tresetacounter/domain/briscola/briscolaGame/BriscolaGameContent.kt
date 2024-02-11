package com.premelc.tresetacounter.domain.briscola.briscolaGame

import androidx.annotation.RequiresPermission
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premelc.tresetacounter.R
import com.premelc.tresetacounter.navigation.NavRoutes
import com.premelc.tresetacounter.service.data.BriscolaRound
import com.premelc.tresetacounter.ui.theme.Typography
import com.premelc.tresetacounter.uiComponents.FullActionToolbar
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
    FullActionToolbar(
        title = stringResource(R.string.briscola_game_title),
        leftAction = {},
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
                    navigate(NavRoutes.BriscolaGameCalculator.route.plus("/${viewState.currentSetId}"))
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

        Box(Modifier.weight(1f)) {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                painter = painterResource(id = R.drawable.treseta_cards),
                contentDescription = null,
                alpha = 0.1f
            )
            BriscolaGrid(
                firstTeamPoints = viewState.rounds.sumOf { it.firstTeamPoints },
                secondTeamPoints = viewState.rounds.sumOf { it.secondTeamPoints },
            )
        }
    }
    Spacer(Modifier)
}

@Composable
private fun BriscolaGrid(firstTeamPoints: Int, secondTeamPoints: Int) {
    val gridState = rememberLazyGridState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 8.dp),
        state = gridState,
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.Center,
    ) {
        items(8) { cellNumber ->
            GridItem(
                drawPoint = shouldDrawPoint(
                    noOfFirstTeamPoints = firstTeamPoints,
                    noOfSecondTeamPoints = secondTeamPoints,
                    fieldNumber = cellNumber
                ),
                cellNumber = cellNumber
            )
        }
    }
}

@Composable
private fun GridItem(drawPoint: Boolean, cellNumber: Int) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .height(IntrinsicSize.Min),
    ) {
        if (drawPoint) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colors.primary),
            )
        }
        if (!listOf(6, 7).contains(cellNumber)) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                color = Color.LightGray
            )
        }
        if (cellNumber % 2 == 0) {
            Divider(
                color = Color.LightGray,
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.TopEnd)
                    .width(1.dp)
            )
        }
    }
}

@ReadOnlyComposable
@Composable
private fun shouldDrawPoint(noOfFirstTeamPoints: Int, noOfSecondTeamPoints: Int, fieldNumber: Int) =
    when (fieldNumber) {
        0 -> noOfFirstTeamPoints >= 1
        2 -> noOfFirstTeamPoints >= 2
        4 -> noOfFirstTeamPoints >= 3
        6 -> noOfFirstTeamPoints >= 4
        1 -> noOfSecondTeamPoints >= 1
        3 -> noOfSecondTeamPoints >= 2
        5 -> noOfSecondTeamPoints >= 3
        7 -> noOfSecondTeamPoints >= 4
        else -> false
    }
