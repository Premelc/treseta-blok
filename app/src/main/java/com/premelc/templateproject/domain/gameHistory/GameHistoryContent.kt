package com.premelc.templateproject.domain.gameHistory

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.premelc.templateproject.domain.gameCalculator.Team
import com.premelc.templateproject.service.data.GameSet
import com.premelc.templateproject.ui.theme.Typography
import com.premelc.templateproject.uiComponents.Accordion
import com.premelc.templateproject.uiComponents.CallsList
import com.premelc.templateproject.uiComponents.TresetaToolbarScaffold
import com.premelc.templateproject.uiComponents.getItemViewportOffset
import com.premelc.templateproject.uiComponents.graph.Graph
import com.premelc.templateproject.uiComponents.parseTimestamp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun GameHistoryScreen(navController: NavController) {
    val viewModel: GameHistoryViewModel = getViewModel { parametersOf(navController) }
    val viewState = viewModel.viewState.collectAsStateWithLifecycle().value
    GameHistoryContent(viewState, viewModel::onInteraction)
}

@Composable
private fun GameHistoryContent(
    viewState: GameHistoryViewState,
    onInteraction: (GameHistoryInteraction) -> Unit
) {
    TresetaToolbarScaffold(
        backAction = {
            onInteraction(GameHistoryInteraction.OnBackButtonClicked)
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
                    text = "Povijest igre",
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
                        Text(text = "MI", style = Typography.h6)
                        Text(text = "VI", style = Typography.h6)
                    }
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
                            title = "${index + 1}. set",
                            isExpanded = expanded.value == index,
                            onFinished = {
                                scope.launch {
                                    val offset = listState.getItemViewportOffset(index)
                                    if (offset != null) listState.animateScrollBy(
                                        value = offset.toFloat(),
                                        animationSpec = tween(500)
                                    )
                                    else listState.animateScrollToItem(index)
                                }
                            },
                            onClick = {
                                expanded.value = if (expanded.value == index) null
                                else index
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
    set: GameSet
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
            Graph(
                modifier = Modifier.height(250.dp),
                xValues = (0..set.roundsList.size.coerceAtLeast(5) + 1).toList(),
                firstTeamPoints = pointsAfterRoundFirstTeam,
                secondTeamPoints = pointsAfterRoundSecondTeam,
            )
            Text(
                text = "Zadnja partija: ${set.roundsList.last().timestamp.parseTimestamp()}",
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 0.25.sp,
                ),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            )
            set.roundsList.forEachIndexed { index, round ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CallsList(horizontalArrangement = Arrangement.End, calls = round.firstTeamCalls)
                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f),
                        text = "${round.firstTeamPoints} : ${round.secondTeamPoints}"
                    )
                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f),
                        text = "${pointsAfterRoundFirstTeam[index]} : ${pointsAfterRoundSecondTeam[index]}"
                    )
                    CallsList(
                        horizontalArrangement = Arrangement.Start,
                        calls = round.secondTeamCalls
                    )
                }
                Divider(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth(), color = Color.LightGray
                )
            }
        }
    }
}

private fun GameSet.getTeamTotalPointsPerRound(team: Team) = buildList {
    var totalPoints = 0
    add(totalPoints)
    this@getTeamTotalPointsPerRound.roundsList.forEachIndexed { index, round ->
        totalPoints += if (team == Team.FIRST) round.firstTeamPoints else round.secondTeamPoints
        this.add(totalPoints)
    }
}