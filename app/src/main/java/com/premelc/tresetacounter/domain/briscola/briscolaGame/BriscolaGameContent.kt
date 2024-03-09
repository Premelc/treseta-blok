package com.premelc.tresetacounter.domain.briscola.briscolaGame

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.premelc.tresetacounter.R
import com.premelc.tresetacounter.ui.theme.Typography
import com.premelc.tresetacounter.uiComponents.FullActionToolbar
import com.premelc.tresetacounter.utils.Team
import com.premelc.tresetacounter.utils.WakeLock
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun BriscolaGameScreen(navController: NavController) {
    val viewModel: BriscolaGameViewModel = koinViewModel { parametersOf(navController) }
    val viewState = viewModel.viewState.collectAsStateWithLifecycle().value
    if (viewState is BriscolaGameViewState.GameReady) {
        BriscolaGameContent(viewState, viewModel::onInteraction)
    }
}

@Composable
internal fun BriscolaGameContent(
    viewState: BriscolaGameViewState.GameReady,
    onInteraction: (BriscolaGameInteraction) -> Unit,
) {
    WakeLock()
    FullActionToolbar(
        title = stringResource(R.string.briscola_game_title),
        leftAction = {
            Icon(
                modifier = Modifier.clickable { onInteraction(BriscolaGameInteraction.TapOnMenuButton) },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
            )
        },
        rightAction = {},
    ) {
        Column {
            PointsList(viewState)
            Spacer(Modifier)
            IncreasePointsButtons(onInteraction)
        }
    }
    if (viewState.showSetFinishedModal) {
        SetFinishedDialog(
            winningTeam = viewState.winningTeam,
            onInteraction = onInteraction
        )
    }
}

@Composable
private fun SetFinishedDialog(
    winningTeam: Team,
    onInteraction: (BriscolaGameInteraction) -> Unit
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
                    onInteraction(BriscolaGameInteraction.TapOnSetFinishedModalConfirm)
                },
            ) {
                Text(stringResource(R.string.game_set_finished_modal_positive))
            }
        }
    }
}

@Composable
private fun IncreasePointsButtons(interaction: (BriscolaGameInteraction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 30.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        AddSubtractButtons(
            modifier = Modifier.padding(end = 4.dp),
            team = Team.FIRST,
            interaction = interaction
        )
        AddSubtractButtons(
            modifier = Modifier.padding(start = 4.dp),
            team = Team.SECOND,
            interaction = interaction
        )
    }
}

@Composable
private fun RowScope.AddSubtractButtons(
    modifier: Modifier = Modifier,
    team: Team,
    interaction: (BriscolaGameInteraction) -> Unit,
) {
    Box(modifier.weight(1f)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            EditPointsButton(painterResource(R.drawable.plus)) {
                interaction(BriscolaGameInteraction.TapOnAddPointButton(team))
            }
            EditPointsButton(painterResource(R.drawable.minus)) {
                interaction(BriscolaGameInteraction.TapOnSubtractPointButton(team))
            }
        }
    }
}

@Composable
private fun RowScope.EditPointsButton(
    painter: Painter,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.weight(1f),
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
    }
}

@Composable
internal fun ColumnScope.PointsList(
    viewState: BriscolaGameViewState.GameReady,
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
                firstTeamPoints = viewState.firstTeamCurrentSetScore,
                secondTeamPoints = viewState.secondTeamCurrentSetScore,
            )
        }
    }
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
        var shouldAnimate by remember { mutableStateOf(false) }

        LaunchedEffect(drawPoint) {
            shouldAnimate = drawPoint
        }
        PointCircle(shouldAnimate)
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

@Composable
private fun BoxScope.PointCircle(shouldAnimate: Boolean) {
    val scale by animateFloatAsState(if (shouldAnimate) 1f else 0f, label = "")

    Box(
        modifier = Modifier
            .size((40 * scale).dp) // scale the size based on the animation value
            .align(Alignment.Center)
            .clip(CircleShape)
            .background(color = MaterialTheme.colors.primary)
            .animateContentSize() // animate size changes
    )
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
