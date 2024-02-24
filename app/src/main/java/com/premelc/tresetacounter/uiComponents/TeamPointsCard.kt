package com.premelc.tresetacounter.uiComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.premelc.tresetacounter.R
import com.premelc.tresetacounter.ui.theme.ColorPalette
import com.premelc.tresetacounter.ui.theme.Typography
import com.premelc.tresetacounter.utils.Call

@Composable
fun RowScope.TeamPointCard(
    team: String,
    isSelected: Boolean,
    pointValue: Int?,
    callsValue: Int? = null,
    isEnabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .border(2.dp, if (isSelected) ColorPalette.mossGreen else Color.Transparent)
            .weight(1f),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.clickable(isEnabled) { onClick() },
            elevation = 10.dp,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = team,
                    style = Typography.h6.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colors.background)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = pointValue?.toString() ?: "0",
                        style = Typography.h6.merge(
                            TextStyle(
                                textAlign = TextAlign.Center,
                                fontWeight = if (pointValue != null) FontWeight.Bold else FontWeight.Thin
                            )
                        ),
                    )
                    if (callsValue != null && callsValue > 0) {
                        Text(
                            modifier = Modifier
                                .padding(end = 24.dp)
                                .align(Alignment.CenterEnd),
                            text = "+ $callsValue",
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Italic,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Calls(
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
            text = stringResource(R.string.game_calculator_calls_napola),
            onClick = { onInteraction(Call.NAPOLITANA) },
        )
        CallButton(
            text = stringResource(R.string.game_calculator_calls_x3),
            onClick = { onInteraction(Call.X3) },
        )
        CallButton(
            text = stringResource(R.string.game_calculator_calls_x4),
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
