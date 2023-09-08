package com.premelc.templateproject.domain.gameCalculator

import android.telecom.Call
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.premelc.templateproject.ui.theme.Typography
import com.premelc.templateproject.uiComponents.TresetaToolbarScaffold
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun GameCalculatorScreen(navController: NavController) {
    val viewModel: GameCalculatorViewModel = koinViewModel { parametersOf(navController) }
    GameCalculatorContent()
}

@Composable
private fun GameCalculatorContent() {
    val pointValue = remember { mutableIntStateOf(0) }
    TresetaToolbarScaffold {
        Column {
            Row {
                TeamPointCard(team = "MI", pointValue = pointValue.value, onValueChange = {})
                TeamPointCard(team = "VI", pointValue = pointValue.value, onValueChange = {})
            }
            Row {
                CallsColumn()
                CallsColumn()
            }
        }
    }
}

@Composable
private fun RowScope.CallsColumn() {
    Text(text = "calls")
}

@Composable
private fun RowScope.TeamPointCard(
    team: String,
    pointValue: Int,
    onValueChange: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .weight(1f),
        contentAlignment = Alignment.Center,
    ) {
        Card(elevation = 10.dp) {
            Column {
                Text(
                    text = team,
                    style = Typography.h6.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                TextField(
                    value = TextFieldValue(
                        text = pointValue.toString(),
                        selection = TextRange(pointValue.toString().length),
                    ),
                    onValueChange = {
                        onValueChange(it.text.formatPointValue())
                    },
                    textStyle = Typography.h6,
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

// TODO(figure this out)
private fun String.formatPointValue() = this.toInt()