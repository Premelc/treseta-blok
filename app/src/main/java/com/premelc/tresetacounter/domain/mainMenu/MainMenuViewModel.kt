package com.premelc.tresetacounter.domain.mainMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premelc.tresetacounter.service.BriscolaService
import com.premelc.tresetacounter.service.TresetaService
import com.premelc.tresetacounter.utils.GameType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainMenuViewModel(
    private val tresetaService: TresetaService,
    private val briscolaService: BriscolaService,
) : ViewModel() {

    val viewState = combine(
        tresetaService.gamesFlow(),
        briscolaService.gamesFlow(),
    ) { tresetaGamesList, briscolaGamesList ->
        MainMenuViewState(
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
                    tresetaService.deleteGame(interaction.gameId)
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
                    tresetaService.toggleGameFavoriteState(
                        interaction.gameId
                    )
                }

        }
    }
}