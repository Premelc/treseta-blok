package com.premelc.tresetacounter.uiComponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.premelc.tresetacounter.R

@Composable
internal fun LanguageAccordion(
    isExpanded: Boolean,
    onHeaderTap: () -> Unit,
    selectedLanguage: String,
    modifier: Modifier = Modifier,
    rowContent: @Composable () -> Unit,
) {
    Column(
        modifier.wrapContentHeight()
    ) {
        val rotation = animateFloatAsState(
            targetValue = if (isExpanded) 0f else 180f,
            animationSpec = tween(DEFAULT_ANIMATION_DURATION),
            finishedListener = {},
            label = "",
        )
        Card(
            modifier = Modifier
                .wrapContentHeight()
                .clickable {
                    onHeaderTap()
                },
            shape = RoundedCornerShape(16.dp),
            elevation = 5.dp
        ) {
            Column(
                modifier = Modifier.width(IntrinsicSize.Min)
            ) {
                Row(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(
                            when (selectedLanguage) {
                                "hr" -> R.drawable.croatia
                                "it" -> R.drawable.italy
                                else -> R.drawable.uk
                            }
                        ),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = stringResource(
                            when (selectedLanguage) {
                                "hr" -> R.string.language_name_croatian
                                "it" -> R.string.language_name_italian
                                else -> R.string.language_name_english
                            }
                        ),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 0.25.sp,
                        )
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        modifier = Modifier.rotate(rotation.value),
                        contentDescription = null
                    )
                }
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn(animationSpec = tween(DEFAULT_ANIMATION_DURATION)) + expandVertically(
                        animationSpec = tween(DEFAULT_ANIMATION_DURATION)
                    ),
                    exit = fadeOut(animationSpec = tween(DEFAULT_ANIMATION_DURATION)) + shrinkVertically(
                        animationSpec = tween(
                            DEFAULT_ANIMATION_DURATION
                        )
                    ),
                ) {
                    rowContent()
                }
            }
        }
    }
}

@Composable
fun Accordion(
    title: String,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onFinished: () -> Unit = {},
    rowContent: @Composable () -> Unit,
) {
    Column(
        modifier
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
            enter = fadeIn(animationSpec = tween(DEFAULT_ANIMATION_DURATION)) + expandVertically(
                animationSpec = tween(DEFAULT_ANIMATION_DURATION)
            ),
            exit = fadeOut(animationSpec = tween(DEFAULT_ANIMATION_DURATION)) + shrinkVertically(
                animationSpec = tween(
                    DEFAULT_ANIMATION_DURATION
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
        animationSpec = tween(DEFAULT_ANIMATION_DURATION),
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
            Surface(shape = CircleShape, color = MaterialTheme.colors.surface) {
                Icon(
                    Icons.Outlined.ArrowDropDown,
                    contentDescription = "arrow-down",
                    modifier = Modifier.rotate(rotation.value),
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}
