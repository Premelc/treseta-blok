package com.premelc.tresetacounter.domain.mainMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premelc.tresetacounter.data.PreferencesManager
import com.premelc.tresetacounter.service.BriscolaService
import com.premelc.tresetacounter.service.TresetaService
import com.premelc.tresetacounter.utils.GameType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val PREFS_LANGUAGE_KEY = "selected_language"

class MainMenuViewModel(
    private val tresetaService: TresetaService,
    private val briscolaService: BriscolaService,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    private val selectedLanguage =
        MutableStateFlow(preferencesManager.getData(PREFS_LANGUAGE_KEY, "en"))

    val viewState = combine(
        tresetaService.gamesFlow(),
        briscolaService.gamesFlow(),
        selectedLanguage,
    ) { tresetaGamesList, briscolaGamesList, selectedLanguage ->
        MainMenuViewState(
            selectedLanguage = selectedLanguage,
            tresetaGames = tresetaGamesList?.sortedByDescending { it.timestamp } ?: emptyList(),
            briscolaGames = briscolaGamesList?.sortedByDescending { it.timestamp } ?: emptyList(),
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        MainMenuViewState(),
    )

    internal fun onInteraction(interaction: MainMenuInteraction) {
        when (interaction) {
            is MainMenuInteraction.OnNewGameClicked -> {
                viewModelScope.launch {
                    when (interaction.gameTypeSelected) {
                        GameType.TRESETA -> tresetaService.startNewGame()
                        GameType.BRISCOLA -> briscolaService.startNewGame()
                    }
                }
            }

            is MainMenuInteraction.TapOnDeleteButton -> {
                viewModelScope.launch {
                    when (interaction.gameType) {
                        GameType.TRESETA -> tresetaService.deleteGame(interaction.gameId)
                        GameType.BRISCOLA -> briscolaService.deleteGame(interaction.gameId)
                    }
                }
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
                selectedLanguage.value = interaction.languageCode
                preferencesManager.saveData(PREFS_LANGUAGE_KEY, interaction.languageCode)
            }
        }
    }
}
