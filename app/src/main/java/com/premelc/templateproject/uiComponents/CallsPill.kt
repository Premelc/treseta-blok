package com.premelc.templateproject.uiComponents

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import com.premelc.templateproject.domain.gameCalculator.Call
import kotlinx.coroutines.launch

@Composable
fun CallsPill(
    modifier: Modifier = Modifier,
    call: Call
) {
    Card(
        modifier = modifier.padding(4.dp),
        shape = RoundedCornerShape(5.dp),
        elevation = 5.dp
    ) {
        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = when (call) {
                    Call.NAPOLITANA -> "Napola"
                    Call.X3 -> "X3"
                    Call.X4 -> "X4"
                },
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 0.25.sp,
                )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RowScope.CallsList(
    horizontalArrangement: Arrangement.Horizontal,
    calls: List<Call>
) {
    FlowRow(
        modifier = Modifier.weight(1f),
        horizontalArrangement = horizontalArrangement,
    ) {
        calls.forEach {
            val scaleAnim = remember { Animatable(0f) }

            LaunchedEffect(calls) {
                calls.forEachIndexed { _, _ ->
                    scaleAnim.animateTo(1f, animationSpec = tween(durationMillis = 250))
                }
            }
            CallsPill(
                modifier = Modifier
                    .scale(scaleAnim.value)
                    .animatePlacement(),
                call = it
            )
        }
    }
}
