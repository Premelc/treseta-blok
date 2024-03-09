package com.premelc.tresetacounter.domain.mainMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premelc.tresetacounter.data.PreferencesManager
import com.premelc.tresetacounter.service.BriscolaService
import com.premelc.tresetacounter.service.TresetaService
import com.premelc.tresetacounter.utils.GameType
import com.premelc.tresetacounter.utils.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val PREFS_LANGUAGE_KEY = "selected_language"
private const val PREFS_SELECTE_GAME_TYPE = "selected_game_type"

class MainMenuViewModel(
    private val tresetaService: TresetaService,
    private val briscolaService: BriscolaService,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    private val selectedLanguageFlow =
        MutableStateFlow(preferencesManager.getData(PREFS_LANGUAGE_KEY, "en"))
    private val selectedGameTypeFlow =
        MutableStateFlow(preferencesManager.getData(PREFS_SELECTE_GAME_TYPE, "0"))
    private val showDeleteGameDialogFlow = MutableStateFlow<Pair<Int, GameType>?>(null)
    private val showTooManyGamesSnackbarFlow = MutableStateFlow(false)

    val viewState = combine(
        tresetaService.gamesFlow(),
        briscolaService.gamesFlow(),
        selectedLanguageFlow,
        showDeleteGameDialogFlow,
        selectedGameTypeFlow,
        showTooManyGamesSnackbarFlow,
    ) { tresetaGamesList, briscolaGamesList, selectedLanguage,
        showDeleteGameDialog, selectedGameType, showTooManyGamesSnackbar ->
        MainMenuViewState(
            selectedLanguage = selectedLanguage,
            tresetaGames = tresetaGamesList?.sortedByDescending { it.timestamp } ?: emptyList(),
            briscolaGames = briscolaGamesList?.sortedByDescending { it.timestamp } ?: emptyList(),
            showDeleteGameDialog = showDeleteGameDialog,
            selectedGameType = selectedGameType.toInt(),
            showTooManyGamesSnackbar = showTooManyGamesSnackbar,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        MainMenuViewState(
            selectedGameType = preferencesManager.getData(PREFS_SELECTE_GAME_TYPE, "0").toInt(),
        ),
    )

    internal fun onInteraction(interaction: MainMenuInteraction) {
        when (interaction) {
            is MainMenuInteraction.OnNewGameClicked -> {
                viewModelScope.launch {
                    if (canCreateNewGame(interaction.gameTypeSelected)) {
                        when (interaction.gameTypeSelected) {
                            GameType.TRESETA -> tresetaService.startNewGame()
                            GameType.BRISCOLA -> briscolaService.startNewGame()
                        }
                    } else {
                        showTooManyGamesSnackbarFlow.value = true
                    }
                }
            }

            is MainMenuInteraction.TapOnDeleteButton -> {
                showDeleteGameDialogFlow.value = Pair(interaction.gameId, interaction.gameType)
            }

            is MainMenuInteraction.TapOnGameCard -> {
                when (interaction.gameType) {
                    GameType.TRESETA -> tresetaService.setSelectedGame(interaction.gameId)
                    GameType.BRISCOLA -> briscolaService.setSelectedGame(interaction.gameId)
                }
            }

            is MainMenuInteraction.TapOnFavoriteButton ->
                viewModelScope.launch {
                    when (interaction.gameType) {
                        GameType.TRESETA -> tresetaService.toggleGameFavoriteState(interaction.gameId)
                        GameType.BRISCOLA -> briscolaService.toggleGameFavoriteState(interaction.gameId)
                    }
                }

            is MainMenuInteraction.TapOnLanguageItem -> {
                selectedLanguageFlow.value = interaction.languageCode
                preferencesManager.saveData(PREFS_LANGUAGE_KEY, interaction.languageCode)
            }

            MainMenuInteraction.TapOnDialogCancel -> {
                showDeleteGameDialogFlow.value = null
            }

            is MainMenuInteraction.TapOnDialogConfirm -> {
                viewModelScope.launch {
                    when (interaction.gameType) {
                        GameType.TRESETA -> tresetaService.deleteGame(interaction.gameId)
                        GameType.BRISCOLA -> briscolaService.deleteGame(interaction.gameId)
                    }
                }
                showDeleteGameDialogFlow.value = null
            }

            is MainMenuInteraction.TapOnGameTypeTab -> {
                selectedGameTypeFlow.value = interaction.tabIndex.toString()
                preferencesManager.saveData(
                    PREFS_SELECTE_GAME_TYPE,
                    interaction.tabIndex.toString()
                )
            }

            MainMenuInteraction.DissmissTooManyGamesSnackbar -> {
                showTooManyGamesSnackbarFlow.value = false
            }
        }
    }

    private suspend fun canCreateNewGame(gameTypeSelected: GameType): Boolean {
        return if (gameTypeSelected == GameType.TRESETA) {
            (tresetaService.gamesFlow().first() ?: emptyList()).size < 20
        } else {
            (briscolaService.gamesFlow().first() ?: emptyList()).size < 20
        }
    }
}
