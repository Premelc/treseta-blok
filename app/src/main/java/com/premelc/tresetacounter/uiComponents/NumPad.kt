package com.premelc.tresetacounter.uiComponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.premelc.tresetacounter.R
import com.premelc.tresetacounter.domain.gameCalculator.GameCalculatorInteraction
import com.premelc.tresetacounter.ui.theme.ColorPalette

@Composable
internal fun BuiltInNumPad(
    isSaveButtonEnabled: Boolean,
    onInteraction: (GameCalculatorInteraction) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 20.dp),
    ) {
        Row {
            NumberField(
                text = "7",
                onClick = { onInteraction(GameCalculatorInteraction.TapOnNumberButton(7)) })
            NumberField(
                text = "8",
                onClick = { onInteraction(GameCalculatorInteraction.TapOnNumberButton(8)) })
            NumberField(
                text = "9",
                onClick = { onInteraction(GameCalculatorInteraction.TapOnNumberButton(9)) })
        }
        Row {
            NumberField(
                text = "4",
                onClick = { onInteraction(GameCalculatorInteraction.TapOnNumberButton(4)) })
            NumberField(
                text = "5",
                onClick = { onInteraction(GameCalculatorInteraction.TapOnNumberButton(5)) })
            NumberField(
                text = "6",
                onClick = { onInteraction(GameCalculatorInteraction.TapOnNumberButton(6)) })
        }
        Row {
            NumberField(
                text = "1",
                onClick = { onInteraction(GameCalculatorInteraction.TapOnNumberButton(1)) })
            NumberField(
                text = "2",
                onClick = { onInteraction(GameCalculatorInteraction.TapOnNumberButton(2)) })
            NumberField(
                text = "3",
                onClick = { onInteraction(GameCalculatorInteraction.TapOnNumberButton(3)) })
        }
        Row {
            NumberField(
                text = stringResource(R.string.game_calculator_delete_label),
                backgroundColor = ColorPalette.richRed,
                onClick = { onInteraction(GameCalculatorInteraction.TapOnDeleteButton) })
            NumberField(
                text = "0",
                onClick = { onInteraction(GameCalculatorInteraction.TapOnNumberButton(0)) })
            NumberField(
                text = stringResource(R.string.game_calculator_save_label),
                backgroundColor = if (isSaveButtonEnabled) ColorPalette.coolGreen else ColorPalette.neutralGray,
                isEnabled = isSaveButtonEnabled,
                onClick = { onInteraction(GameCalculatorInteraction.TapOnSaveButton) })
        }
    }
}

@Composable
private fun RowScope.NumberField(
    text: String,
    backgroundColor: Color = MaterialTheme.colors.background,
    isEnabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Card(
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = 10.dp,
        modifier = Modifier
            .weight(1f)
            .padding(6.dp)
            .heightIn(60.dp)
            .clickable(enabled = isEnabled) { onClick() },
        backgroundColor = backgroundColor,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text)
        }
    }
}