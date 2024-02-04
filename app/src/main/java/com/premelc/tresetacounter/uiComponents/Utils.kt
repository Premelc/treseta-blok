package com.premelc.tresetacounter.uiComponents

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
@ReadOnlyComposable
fun Long?.parseTimestamp(): String {
    return if (this == null) " - "
    else when (val difference = System.currentTimeMillis() - this) {
        in Long.MIN_VALUE..60000 -> "Upravo"
        in 60001..3600000 -> "Prije ${difference / 60000} minuta"
        in 3600001..4000000 -> "Prije sat vremena"
        in 4000001..14400000 -> "Prije ${difference / 3600000} sata"
        in 14400001..Long.MAX_VALUE -> {
            val milliseconds = this // Example milliseconds

            val instant = Instant.ofEpochMilli(milliseconds)
            val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

            val formatter = DateTimeFormatter.ofPattern("dd. MMMM yyyy. HH:mm", Locale.getDefault())
            date.format(formatter)
        }

        else -> ""
    }
}

fun LazyListState.getItemViewportOffset(index: Int): Int? =
    layoutInfo.visibleItemsInfo.find { it.index == index }
        ?.let {
            when {
                it.size + it.offset > layoutInfo.viewportEndOffset ->
                    (it.size + it.offset) - layoutInfo.viewportEndOffset

                it.offset < layoutInfo.viewportStartOffset -> it.offset - layoutInfo.viewportStartOffset
                else -> 0
            }
        }

fun Modifier.animatePlacement(): Modifier = composed {
    val scope = rememberCoroutineScope()
    var targetOffset by remember { mutableStateOf(IntOffset.Zero) }
    var animatable by remember {
        mutableStateOf<Animatable<IntOffset, AnimationVector2D>?>(null)
    }
    this
        .onPlaced {
            targetOffset = it
                .positionInParent()
                .round()
        }
        .offset {
            val anim = animatable ?: Animatable(targetOffset, IntOffset.VectorConverter)
                .also { animatable = it }
            if (anim.targetValue != targetOffset) {
                scope.launch {
                    anim.animateTo(targetOffset, spring(stiffness = Spring.StiffnessHigh))
                }
            }
            animatable?.let { it.value - targetOffset } ?: IntOffset.Zero
        }
}