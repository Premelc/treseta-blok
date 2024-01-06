package com.premelc.templateproject.domain.gameCalculator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.premelc.templateproject.ui.theme.Typography
import com.premelc.templateproject.uiComponents.TresetaToolbarScaffold
import com.premelc.templateproject.domain.gameCalculator.GameCalculatorInteraction.TapOnNumberButton
import com.premelc.templateproject.domain.gameCalculator.GameCalculatorInteraction.TapOnDeleteButton
import com.premelc.templateproject.domain.gameCalculator.GameCalculatorInteraction.TapOnSaveButton
import com.premelc.templateproject.domain.gameCalculator.GameCalculatorInteraction.TapOnCallButton
import com.premelc.templateproject.domain.gameCalculator.GameCalculatorInteraction.TapOnTeamCard
import com.premelc.templateproject.uiComponents.CallsList
import com.premelc.templateproject.uiComponents.animatePlacement
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun GameCalculatorScreen(navController: NavController, setId: Int) {
    val viewModel: GameCalculatorViewModel = koinViewModel { parametersOf(navController, setId) }
    val viewState = viewModel.viewState.collectAsStateWithLifecycle().value
    GameCalculatorContent(viewState, viewModel::onInteraction)
}

@Composable
private fun GameCalculatorContent(
    viewState: GameCalculatorViewState,
    onInteraction: (GameCalculatorInteraction) -> Unit,
) {
    TresetaToolbarScaffold(backAction = { onInteraction(GameCalculatorInteraction.TapOnBackButton) }) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(Modifier.padding(horizontal = 20.dp)) {
                CallsList(
                    horizontalArrangement = Arrangement.Start,
                    calls = viewState.firstTeamCalls
                )
                CallsList(
                    horizontalArrangement = Arrangement.End,
                    calls = viewState.secondTeamCalls
                )
            }
            Column(modifier = Modifier.animatePlacement()) {
                Row(Modifier.padding(20.dp)) {
                    TeamPointCard(
                        team = "MI",
                        pointValue = viewState.firstTeamScore,
                        isSelected = viewState.selectedTeam == Team.FIRST,
                        onClick = {
                            onInteraction(TapOnTeamCard(Team.FIRST))
                        },
                    )
                    TeamPointCard(
                        team = "VI",
                        pointValue = viewState.secondTeamScore,
                        isSelected = viewState.selectedTeam == Team.SECOND,
                        onClick = {
                            onInteraction(TapOnTeamCard(Team.SECOND))
                        },
                    )
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Zvanja",
                )
                Calls(
                    onInteraction = { call ->
                        onInteraction(TapOnCallButton(call))
                    },
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            BuiltInNumPad(
                onInteraction = onInteraction,
                isSaveButtonEnabled = viewState.isSaveButtonEnabled,
            )
        }
    }
}

@Composable
private fun Calls(
    onInteraction: (Call) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 20.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        CallButton(
            text = "Napola",
            onClick = { onInteraction(Call.NAPOLITANA) },
        )
        CallButton(
            text = "x3",
            onClick = { onInteraction(Call.X3) },
        )
        CallButton(
            text = "x4",
            onClick = { onInteraction(Call.X4) },
        )
    }
}

@Composable
private fun CallButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        modifier = Modifier.defaultMinSize(minHeight = 40.dp, minWidth = 84.dp),
        onClick = onClick,
    ) {
        Text(text = text)
    }
}

@Composable
private fun RowScope.TeamPointCard(
    team: String,
    isSelected: Boolean,
    pointValue: Int?,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .border(2.dp, if (isSelected) Color.Green else Color.Transparent)
            .weight(1f),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.clickable { onClick() },
            elevation = 10.dp,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = team,
                    style = Typography.h6.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                TextField(
                    value = TextFieldValue(
                        text = pointValue?.toString() ?: "0",
                    ),
                    onValueChange = {},
                    enabled = false,
                    textStyle = Typography.h6.merge(
                        TextStyle(
                            textAlign = TextAlign.Center,
                            fontWeight = if (pointValue != null) FontWeight.Bold else FontWeight.Thin
                        )
                    ),
                    label = {},
                    placeholder = {
                        Text(
                            modifier = Modifier.alpha(0.5f),
                            text = "0",
                            style = Typography.h6
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    interactionSource = remember { MutableInteractionSource() },
                )
            }
        }
    }
}

@Composable
private fun BuiltInNumPad(
    isSaveButtonEnabled: Boolean,
    onInteraction: (GameCalculatorInteraction) -> Unit,
) {
    Card(
        modifier = Modifier.wrapContentSize()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 20.dp),
        ) {
            Row {
                NumberField(
                    text = "7",
                    onClick = { onInteraction(TapOnNumberButton(7)) })
                NumberField(
                    text = "8",
                    onClick = { onInteraction(TapOnNumberButton(8)) })
                NumberField(
                    text = "9",
                    onClick = { onInteraction(TapOnNumberButton(9)) })
            }
            Row {
                NumberField(
                    text = "4",
                    onClick = { onInteraction(TapOnNumberButton(4)) })
                NumberField(
                    text = "5",
                    onClick = { onInteraction(TapOnNumberButton(5)) })
                NumberField(
                    text = "6",
                    onClick = { onInteraction(TapOnNumberButton(6)) })
            }
            Row {
                NumberField(
                    text = "1",
                    onClick = { onInteraction(TapOnNumberButton(1)) })
                NumberField(
                    text = "2",
                    onClick = { onInteraction(TapOnNumberButton(2)) })
                NumberField(
                    text = "3",
                    onClick = { onInteraction(TapOnNumberButton(3)) })
            }
            Row {
                NumberField(
                    text = "Obrisi",
                    backgroundColor = Color.Red,
                    onClick = { onInteraction(TapOnDeleteButton) })
                NumberField(
                    text = "0",
                    onClick = { onInteraction(TapOnNumberButton(0)) })
                NumberField(
                    text = "Spremi",
                    backgroundColor = if (isSaveButtonEnabled) Color.Green else Color.Gray,
                    onClick = { if (isSaveButtonEnabled) onInteraction(TapOnSaveButton) })
            }
        }
    }
}

@Composable
private fun RowScope.NumberField(
    text: String,
    backgroundColor: Color = MaterialTheme.colors.surface,
    onClick: () -> Unit = {}
) {
    Card(
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = 10.dp,
        modifier = Modifier
            .weight(1f)
            .padding(6.dp)
            .heightIn(60.dp)
            .clickable { onClick() },
        backgroundColor = backgroundColor,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = text)
        }
    }
}
