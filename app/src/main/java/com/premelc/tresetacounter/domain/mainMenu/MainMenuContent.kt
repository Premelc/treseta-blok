package com.premelc.tresetacounter.domain.mainMenu

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premelc.tresetacounter.R
import com.premelc.tresetacounter.data.GameEntity
import com.premelc.tresetacounter.navigation.NavRoutes
import com.premelc.tresetacounter.ui.theme.ColorPalette
import com.premelc.tresetacounter.ui.theme.Typography
import com.premelc.tresetacounter.uiComponents.TresetaToolbarScaffold
import com.premelc.tresetacounter.uiComponents.parseTimestamp
import org.koin.androidx.compose.getViewModel

@Composable
fun MainMenuScreen(navigate: (String) -> Unit) {
    val viewModel: MainMenuViewModel = getViewModel()
    val viewState = viewModel.viewState.collectAsStateWithLifecycle().value
    MainMenuContent(viewState, viewModel::onInteraction, navigate)
}

@Composable
private fun MainMenuContent(
    viewState: MainMenuViewState,
    onInteraction: (MainMenuInteraction) -> Unit,
    navigate: (String) -> Unit,
) {
    TresetaToolbarScaffold {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .weight(1f),
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp),
                            painter = painterResource(id = R.drawable.treseta_cards),
                            contentDescription = null,
                        )
                    }
                    Text(
                        text = "Nastavi prijasnju igru",
                        style = Typography.h6,
                        modifier = Modifier.padding(bottom = 4.dp, start = 20.dp)
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                    )
                }
                if (viewState.games.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxSize(1f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                modifier = Modifier.padding(top = 150.dp),
                                text = "Nemate prijasnjih igara",
                                color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Zapocni novu igru pritiskom na tipku",
                                color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    item {
                        PastGameContent(
                            games = viewState.games,
                            onInteraction = onInteraction,
                            navigate = navigate,
                        )
                    }
                }
            }
            Button(
                onClick = {
                    onInteraction(MainMenuInteraction.OnNewGameClicked)
                    navigate(NavRoutes.TresetaGame.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(60.dp),
            ) {
                Text(text = stringResource(R.string.new_game_button))
            }
        }
    }
}

@Composable
private fun PastGameContent(
    games: List<GameEntity>,
    onInteraction: (MainMenuInteraction) -> Unit,
    navigate: (String) -> Unit,
) {
    Column {
        if (games.any { it.isFavorite }) {
            Text(
                text = "Favoriti",
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 0.15.sp
                ),
                modifier = Modifier.padding(bottom = 4.dp, start = 20.dp)
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
            )
            games.filter { it.isFavorite }.forEach {
                PastGameCard(
                    game = it,
                    onInteraction = onInteraction,
                    navigate = navigate,
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
            )
        }
        games.filter { !it.isFavorite }.forEach {
            PastGameCard(
                game = it,
                onInteraction = onInteraction,
                navigate = navigate,
            )
        }
    }
}


@Composable
private fun PastGameCard(
    game: GameEntity,
    onInteraction: (MainMenuInteraction) -> Unit,
    navigate: (String) -> Unit,
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
                    navigate(NavRoutes.TresetaGame.route)
                    onInteraction(MainMenuInteraction.TapOnGameCard(game.id))
                },
            elevation = 10.dp,
            shape = RoundedCornerShape(5.dp)
        ) {
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
                                    game.id
                                )
                            )
                        },
                    painter = if (game.isFavorite) painterResource(R.drawable.star_full) else painterResource(
                        R.drawable.star_empty
                    ),
                    contentDescription = null,
                    tint = ColorPalette.goldenYellow,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly,
                ) {
                    if (game.timestamp != null) {
                        Text(
                            text = "Zadnja partija: ${game.timestamp.parseTimestamp()}",
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 10.sp,
                                fontStyle = FontStyle.Italic,
                                letterSpacing = 0.25.sp,
                            ),
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                        )
                    }
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
                                    append("Mi ")
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
                                    append(" Vi")
                                }
                            },
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    Icon(
                        modifier = Modifier
                            .size(34.dp)
                            .clickable { onInteraction(MainMenuInteraction.TapOnDeleteButton(game.id)) },
                        painter = painterResource(R.drawable.trash),
                        contentDescription = null,
                        tint = ColorPalette.richRed,
                    )
                }
            }
        }
    }
}
