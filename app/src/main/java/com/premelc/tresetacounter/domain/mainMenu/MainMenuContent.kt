package com.premelc.tresetacounter.domain.mainMenu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premelc.tresetacounter.R
import com.premelc.tresetacounter.data.GameEntity
import com.premelc.tresetacounter.navigation.NavRoutes
import com.premelc.tresetacounter.ui.theme.Typography
import com.premelc.tresetacounter.uiComponents.LanguageDropDownMenu
import com.premelc.tresetacounter.uiComponents.PastGameCard
import com.premelc.tresetacounter.uiComponents.ToolbarScaffold
import com.premelc.tresetacounter.utils.GameType
import kotlinx.coroutines.launch
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
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage = stringResource(R.string.main_menu_too_many_games_snackbar)
    ToolbarScaffold(
        snackbarHostState = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(
                Modifier.weight(1f)
            ) {
                item {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Column(
                            modifier = Modifier
                                .padding(top = 20.dp)
                                .fillMaxWidth()
                                .height(250.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Image(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(350.dp),
                                painter = painterResource(R.drawable.treseta_cards),
                                contentDescription = null,
                            )
                        }
                        LanguageDropDownMenu(
                            modifier = Modifier
                                .padding(12.dp)
                                .align(Alignment.TopEnd),
                            selectedLanguage = viewState.selectedLanguage,
                            onInteraction = onInteraction,
                        )
                    }
                }
                item {
                    GameTypeTabs(
                        viewState = viewState,
                        selectedIndex = viewState.selectedGameType,
                        onTabSelection = { selectedIndex ->
                            onInteraction(MainMenuInteraction.TapOnGameTypeTab(selectedIndex))
                        },
                        onInteraction = onInteraction,
                        navigate = navigate
                    )
                }
            }
            NewGameButton(
                onInteraction = onInteraction,
                navigate = navigate,
                selectedIndex = viewState.selectedGameType,
                isEnabled = when (viewState.selectedGameType) {
                    0 -> viewState.tresetaGames.size < 20
                    else -> viewState.briscolaGames.size < 20
                },
            )
        }
    }
    if (viewState.showDeleteGameDialog != null) {
        DeleteGameDialog(
            onInteraction = onInteraction,
            gameId = viewState.showDeleteGameDialog.first,
            gameType = viewState.showDeleteGameDialog.second,
        )
    }
    LaunchedEffect(viewState.showTooManyGamesSnackbar) {
        if (viewState.showTooManyGamesSnackbar) {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = snackbarMessage,
                    duration = SnackbarDuration.Short
                )
                when (result) {
                    SnackbarResult.Dismissed, SnackbarResult.ActionPerformed -> {
                        onInteraction(MainMenuInteraction.DissmissTooManyGamesSnackbar)
                    }
                }
            }
        }
    }
}

@Composable
private fun NewGameButton(
    onInteraction: (MainMenuInteraction) -> Unit,
    navigate: (String) -> Unit,
    isEnabled: Boolean,
    selectedIndex: Int,
) {
    Button(
        onClick = {
            onInteraction(
                MainMenuInteraction.OnNewGameClicked(
                    if (selectedIndex == 0) GameType.TRESETA else GameType.BRISCOLA
                )
            )
            if (isEnabled) {
                navigate(
                    if (selectedIndex == 0) {
                        NavRoutes.TresetaGame.route
                    } else {
                        NavRoutes.BriscolaGame.route
                    }
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .height(60.dp),
    ) {
        Text(
            text = stringResource(R.string.main_menu_new_game_button),
            color = MaterialTheme.colors.onPrimary.copy(alpha = if (isEnabled) 1.0f else 0.5f)
        )
    }
}

@Composable
private fun GameTypeTabs(
    viewState: MainMenuViewState,
    selectedIndex: Int,
    onTabSelection: (Int) -> Unit,
    onInteraction: (MainMenuInteraction) -> Unit,
    navigate: (String) -> Unit,
) {
    val tabs = listOf(
        R.string.main_menu_treseta_tab_title,
        R.string.main_menu_briscola_tab_title,
    )
    TabRow(
        modifier = Modifier.padding(bottom = 24.dp),
        selectedTabIndex = selectedIndex,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground,
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                text = { Text(stringResource(title)) },
                selected = selectedIndex == index,
                onClick = {
                    onTabSelection(index)
                }
            )
        }
    }
    when (selectedIndex) {
        0 -> TabContent(
            gameList = viewState.tresetaGames,
            onInteraction = onInteraction,
            navigate = {
                navigate(NavRoutes.TresetaGame.route)
            },
        )

        1 -> TabContent(
            gameList = viewState.briscolaGames,
            onInteraction = onInteraction,
            navigate = {
                navigate(NavRoutes.BriscolaGame.route)
            },
        )
    }
}

@Composable
private fun TabContent(
    gameList: List<GameEntity>,
    onInteraction: (MainMenuInteraction) -> Unit,
    navigate: () -> Unit,
) {
    if (gameList.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(top = 150.dp),
                text = stringResource(R.string.main_menu_no_previous_games_label),
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        PastGameContent(
            games = gameList,
            onInteraction = onInteraction,
            navigate = navigate,
        )
    }
}

@Composable
private fun PastGameContent(
    games: List<GameEntity>,
    onInteraction: (MainMenuInteraction) -> Unit,
    navigate: () -> Unit,
) {
    Column {
        Text(
            text = stringResource(R.string.main_menu_continue_game_title),
            style = Typography.h6,
            modifier = Modifier.padding(bottom = 4.dp, start = 20.dp)
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
        )
        if (games.any { it.isFavorite }) {
            Text(
                text = stringResource(R.string.main_menu_favourite_label),
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
private fun DeleteGameDialog(
    onInteraction: (MainMenuInteraction) -> Unit,
    gameId: Int,
    gameType: GameType,
) {
    Dialog(
        onDismissRequest = { /*TODO*/ },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        )
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(MaterialTheme.colors.background)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                text = stringResource(R.string.main_menu_dialog_text)
            )
            Button(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .height(60.dp)
                    .fillMaxWidth(),
                onClick = {
                    onInteraction(MainMenuInteraction.TapOnDialogConfirm(gameId, gameType))
                },
            ) {
                Text(text = stringResource(R.string.main_menu_dialog_positive))
            }
            Button(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .height(60.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.background,
                ),
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.onBackground),
                onClick = {
                    onInteraction(MainMenuInteraction.TapOnDialogCancel)
                },
            ) {
                Text(text = stringResource(R.string.main_menu_dialog_negative))
            }
        }
    }
}
