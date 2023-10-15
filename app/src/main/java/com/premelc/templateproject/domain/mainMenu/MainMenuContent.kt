package com.premelc.templateproject.domain.mainMenu

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.premelc.templateproject.R
import com.premelc.templateproject.data.GameEntity
import com.premelc.templateproject.ui.theme.Typography
import com.premelc.templateproject.uiComponents.TresetaToolbarScaffold
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainMenuScreen(navController: NavController) {
    val viewModel: MainMenuViewModel = getViewModel { parametersOf(navController) }
    val viewState = viewModel.viewState.collectAsStateWithLifecycle().value
    MainMenuContent(viewState, viewModel::onInteraction)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MainMenuContent(
    viewState: MainMenuViewState,
    onInteraction: (MainMenuInteraction) -> Unit,
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
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Zapocni novu igru pritiskom na tipku",
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                            )
                        }

                    }
                } else {
                    for (game in viewState.games) {
                        item {
                            PastGameCard(game, onInteraction)
                        }
                    }
                }
            }
            Button(
                onClick = { onInteraction(MainMenuInteraction.OnNewGameClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
            ) {
                Text(text = stringResource(R.string.new_game_button))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun PastGameCard(
    game: GameEntity,
    onInteraction: (MainMenuInteraction) -> Unit,
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
                .clickable { onInteraction(MainMenuInteraction.TapOnGameCard(game.id)) },
            elevation = 10.dp,
            shape = RoundedCornerShape(5.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Text(
                        text = "Zadnja partija: ${game.timestamp.parseTimestamp()}",
                        style = Typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                    )
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
                        tint = Color.Red,
                    )
                }
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun Long?.parseTimestamp(): String {
    return if (this == null) " - "
    else when (val difference = System.currentTimeMillis() - this) {
        in Long.MIN_VALUE..60000 -> "Upravo"
        in 60001..1200000 -> "Prije nekoliko minuta"
        in 1200001..4000000 -> "Prije sat vremena"
        in 4000001..21600000 -> "Prije nekoliko sati"
        in 21600000..Long.MAX_VALUE -> {
            val gameDate = LocalDate.ofEpochDay(difference)
            val nowDate = LocalDate.now()
            when {
                nowDate == gameDate -> "Danas"
                nowDate.minusDays(1) == gameDate -> "Jucer"
                else -> "${gameDate.dayOfMonth}.${gameDate.month}.${gameDate.year}"
            }
        }

        else -> ""
    }
}
