package com.premelc.tresetacounter.uiComponents

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.premelc.tresetacounter.R
import com.premelc.tresetacounter.utils.Call

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
                    Call.NAPOLITANA -> stringResource(R.string.calls_napola)
                    Call.X3 -> stringResource(R.string.calls_x3)
                    Call.X4 -> stringResource(R.string.calls_x4)
                },
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.25.sp,
                )
            )
        }
    }
}

@Composable
fun RemovableCallsPill(
    modifier: Modifier = Modifier,
    call: Call,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(5.dp),
        elevation = 5.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier
                    .size(12.dp)
                    .padding(horizontal = 2.dp),
                imageVector = Icons.Filled.Clear,
                contentDescription = null,
                tint = Color.LightGray,
            )
            Text(
                modifier = Modifier.padding(end = 8.dp),
                text = when (call) {
                    Call.NAPOLITANA -> stringResource(R.string.calls_napola)
                    Call.X3 -> stringResource(R.string.calls_x3)
                    Call.X4 -> stringResource(R.string.calls_x4)
                },
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
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
        modifier = Modifier
            .weight(1f)
            .padding(vertical = 6.dp),
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RowScope.RemovableCallsList(
    horizontalArrangement: Arrangement.Horizontal,
    calls: List<Call>,
    onClick: (Int) -> Unit,
) {
    FlowRow(
        modifier = Modifier
            .weight(1f)
            .padding(vertical = 6.dp),
        horizontalArrangement = horizontalArrangement,
    ) {
        calls.forEachIndexed { index, call ->
            val scaleAnim = remember { Animatable(0f) }

            LaunchedEffect(calls) {
                calls.forEachIndexed { _, _ ->
                    scaleAnim.animateTo(1f, animationSpec = tween(durationMillis = 250))
                }
            }
            RemovableCallsPill(
                modifier = Modifier
                    .scale(scaleAnim.value)
                    .animatePlacement(),
                call = call,
                onClick = {
                    onClick(index)
                },
            )
        }
    }
}
