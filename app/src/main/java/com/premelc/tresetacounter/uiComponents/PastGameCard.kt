package com.premelc.tresetacounter.uiComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.premelc.tresetacounter.R
import com.premelc.tresetacounter.data.GameEntity
import com.premelc.tresetacounter.domain.mainMenu.MainMenuInteraction
import com.premelc.tresetacounter.ui.theme.ColorPalette

@Composable
internal fun PastGameCard(
    game: GameEntity,
    onInteraction: (MainMenuInteraction) -> Unit,
    navigate: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 20.dp)
            .padding(bottom = 16.dp),
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    navigate()
                    onInteraction(MainMenuInteraction.TapOnGameCard(game.id, game.gameType))
                },
            elevation = 10.dp,
            shape = RoundedCornerShape(5.dp)
        ) {
            PastGameCardContent(game, onInteraction)
        }
    }
}

@Composable
private fun PastGameCardContent(game: GameEntity, onInteraction: (MainMenuInteraction) -> Unit) {
    Row(
        modifier = Modifier.padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .size(34.dp)
                .padding(end = 8.dp)
                .clickable {
                    onInteraction(
                        MainMenuInteraction.TapOnFavoriteButton(
                            game.id,
                            game.gameType,
                        )
                    )
                },
            painter = if (game.isFavorite) {
                painterResource(R.drawable.star_full)
            } else {
                painterResource(R.drawable.star_empty)
            },
            contentDescription = null,
            tint = ColorPalette.goldenYellow,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            if (game.timestamp != null) {
                Text(
                    modifier = Modifier.padding(vertical = 6.dp),
                    text = stringResource(
                        R.string.last_game_timestamp,
                        game.timestamp.parseTimestamp()
                    ),
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = 0.25.sp,
                    ),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                )
            }
            PastGameResultContent(game)
        }
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            Icon(
                modifier = Modifier
                    .size(34.dp)
                    .clickable {
                        onInteraction(
                            MainMenuInteraction.TapOnDeleteButton(
                                game.id,
                                game.gameType,
                            )
                        )
                    },
                painter = painterResource(R.drawable.trash),
                contentDescription = null,
                tint = ColorPalette.richRed,
            )
        }
    }
}

@Composable
private fun PastGameResultContent(game: GameEntity) {
    Row(
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp
                    )
                ) {
                    append(stringResource(R.string.main_menu_first_team_label))
                }
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                ) {
                    append("${game.firstTeamPoints}")
                }
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                ) {
                    append(" : ")
                }
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                ) {
                    append("${game.secondTeamPoints}")
                }
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp
                    )
                ) {
                    append(stringResource(R.string.main_menu_second_team_label))
                }
            },
        )
    }
}
