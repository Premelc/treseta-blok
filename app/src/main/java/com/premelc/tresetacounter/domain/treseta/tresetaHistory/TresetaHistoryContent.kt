package com.premelc.tresetacounter.domain.treseta.tresetaHistory

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.premelc.tresetacounter.ui.theme.Typography
import com.premelc.tresetacounter.uiComponents.Accordion
import com.premelc.tresetacounter.uiComponents.CallsList
import com.premelc.tresetacounter.uiComponents.ToolbarScaffold
import com.premelc.tresetacounter.uiComponents.getItemViewportOffset
import com.premelc.tresetacounter.uiComponents.graph.Graph
import com.premelc.tresetacounter.uiComponents.parseTimestamp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import com.premelc.tresetacounter.R
import com.premelc.tresetacounter.service.data.Round
import com.premelc.tresetacounter.service.data.TresetaGameSet
import com.premelc.tresetacounter.service.data.TresetaRound
import com.premelc.tresetacounter.uiComponents.DEFAULT_ANIMATION_DURATION
import com.premelc.tresetacounter.utils.Team

@Composable
fun TresetaGameHistoryScreen(navController: NavController) {
    val viewModel: TresetaHistoryViewModel = getViewModel { parametersOf(navController) }
    val viewState = viewModel.viewState.collectAsStateWithLifecycle().value
    GameHistoryContent(viewState, viewModel::onInteraction)
}

@Composable
private fun GameHistoryContent(
    viewState: TresetaHistoryViewState,
    onInteraction: (TresetaHistoryInteraction) -> Unit
) {
    ToolbarScaffold(
        backAction = {
            onInteraction(TresetaHistoryInteraction.OnBackButtonClicked)
        }
    ) {
        val listState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        val expanded = remember {
            mutableStateOf<Int?>(null)
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Top,
            state = listState,
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.game_history_title),
                    style = Typography.h6.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }
            item {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = stringResource(R.string.game_history_first_team_title),
                            style = Typography.h6
                        )
                        Text(
                            text = stringResource(R.string.game_history_second_team_title),
                            style = Typography.h6
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(
                                R.string.treseta_score,
                                viewState.firstTeamScore,
                                viewState.secondTeamScore
                            ),
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
            viewState.sets.filter { it.roundsList.isNotEmpty() }.sortedBy { it.id }
                .forEachIndexed { index, set ->
                    item {
                        Accordion(
                            modifier = Modifier.padding(vertical = 9.dp),
                            title = stringResource(R.string.game_history_set_number, index + 1),
                            isExpanded = expanded.value == index,
                            onFinished = {
                                scope.launch {
                                    val offset = listState.getItemViewportOffset(index)
                                    if (offset != null) {
                                        listState.animateScrollBy(
                                            value = offset.toFloat(),
                                            animationSpec = tween(DEFAULT_ANIMATION_DURATION)
                                        )
                                    } else {
                                        listState.animateScrollToItem(index)
                                    }
                                }
                            },
                            onClick = {
                                expanded.value = if (expanded.value == index) null else index
                            }
                        ) {
                            SetHistoryContent(set)
                        }
                    }
                }
        }
    }
}

@Composable
private fun SetHistoryContent(
    set: TresetaGameSet
) {
    val pointsAfterRoundFirstTeam = set.getTeamTotalPointsPerRound(Team.FIRST)
    val pointsAfterRoundSecondTeam = set.getTeamTotalPointsPerRound(Team.SECOND)

    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 5.dp),
        shape = RoundedCornerShape(0.dp, 0.dp, 10.dp, 10.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            TotalSetResultContent(pointsAfterRoundFirstTeam, pointsAfterRoundSecondTeam)
            Graph(
                modifier = Modifier.height(250.dp),
                xValues = (0..set.roundsList.size.coerceAtLeast(6) + 1).toList(),
                firstTeamPoints = pointsAfterRoundFirstTeam,
                secondTeamPoints = pointsAfterRoundSecondTeam,
                yValues = (0..10).toList().map { it * 5 },
            )
            Text(
                text = stringResource(
                    R.string.last_game_timestamp,
                    set.roundsList.last().timestamp.parseTimestamp()
                ),
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 0.25.sp,
                ),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            )
            set.roundsList.forEachIndexed { index, round: Round ->
                RoundInHistoryContent(
                    round,
                    index,
                    pointsAfterRoundFirstTeam,
                    pointsAfterRoundSecondTeam
                )
            }
        }
    }
}

@Composable
private fun TotalSetResultContent(
    pointsAfterRoundFirstTeam: List<Int>,
    pointsAfterRoundSecondTeam: List<Int>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = pointsAfterRoundFirstTeam.last().toString(),
            style = Typography.subtitle1,
            textDecoration = if (pointsAfterRoundFirstTeam.last() > pointsAfterRoundSecondTeam.last()) {
                TextDecoration.Underline
            } else {
                TextDecoration.None
            }
        )
        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = ":",
            style = Typography.subtitle1
        )
        Text(
            text = pointsAfterRoundSecondTeam.last().toString(),
            style = Typography.subtitle1,
            textDecoration = if (pointsAfterRoundSecondTeam.last() > pointsAfterRoundFirstTeam.last()) {
                TextDecoration.Underline
            } else {
                TextDecoration.None
            }
        )
    }
}

@Composable
private fun RoundInHistoryContent(
    round: Round,
    index: Int,
    pointsAfterRoundFirstTeam: List<Int>,
    pointsAfterRoundSecondTeam: List<Int>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = CenterVertically
    ) {
        if (round is TresetaRound) {
            CallsList(
                horizontalArrangement = Arrangement.End,
                calls = round.firstTeamCalls
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth()
                .padding(16.dp),
            horizontalAlignment = CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = stringResource(
                    R.string.treseta_score,
                    pointsAfterRoundFirstTeam[index + 1],
                    pointsAfterRoundSecondTeam[index + 1]
                ),
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 8.sp,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 0.25.sp,
                ),
            )
            Text(
                text = stringResource(
                    R.string.treseta_score,
                    round.firstTeamPoints,
                    round.secondTeamPoints
                ),
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                ),
                maxLines = 1,
            )
        }
        if (round is TresetaRound) {
            CallsList(
                horizontalArrangement = Arrangement.Start,
                calls = round.secondTeamCalls
            )
        }
    }
    Divider(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth(),
        color = Color.LightGray
    )
}

private fun TresetaGameSet.getTeamTotalPointsPerRound(team: Team): List<Int> {
    var totalPoints = 0
    val list = mutableListOf(totalPoints)
    this@getTeamTotalPointsPerRound.roundsList.forEach { round ->
        totalPoints += if (team == Team.FIRST) round.firstTeamPoints else round.secondTeamPoints
        list.add(totalPoints)
    }
    return list
}
