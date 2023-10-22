package com.premelc.templateproject.uiComponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Accordion(
    title: String,
    isExpanded: Boolean,
    onClick: () -> Unit = {},
    onFinished: () -> Unit = {},
    rowContent: @Composable () -> Unit,
) {
    Column(
        Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        AccordionHeader(
            title = title,
            expansionState = isExpanded,
            onFinished = onFinished,
            onTapped = onClick
        )
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(animationSpec = tween(500)) + expandVertically(
                animationSpec = tween(500)
            ),
            exit = fadeOut(animationSpec = tween(500)) + shrinkVertically(
                animationSpec = tween(
                    500
                )
            ),
        ) {
            rowContent()
        }
    }
}

@Composable
private fun AccordionHeader(
    title: String,
    expansionState: Boolean,
    onFinished: () -> Unit = {},
    onTapped: () -> Unit = {}
) {
    val rotation = animateFloatAsState(
        targetValue = if (expansionState) 0f else 180f,
        animationSpec = tween(500),
        finishedListener = {
            onFinished()
        },
        label = "",
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        elevation = 10.dp,
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .clickable { onTapped() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, Modifier.weight(1f))
            Surface(shape = CircleShape, color = Color.LightGray) {
                Icon(
                    Icons.Outlined.ArrowDropDown,
                    contentDescription = "arrow-down",
                    modifier = Modifier.rotate(rotation.value),
                    tint = Color.Blue
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccordionPreview() {
    Accordion(
        title = "testTitle",
        rowContent = {},
        onClick = {},
        isExpanded = false,
        onFinished = {},
    )
}
