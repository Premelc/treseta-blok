package com.premelc.templateproject.domain.mainMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.premelc.templateproject.navigation.NavRoutes
import com.premelc.templateproject.service.TresetaService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainMenuViewModel(
    private val tresetaService: TresetaService,
    private val navController: NavController,
) : ViewModel() {

    val viewState =
        tresetaService.gamesFlow().flatMapLatest { gameList ->
            MutableStateFlow(MainMenuViewState(gameList?.sortedByDescending { it.timestamp }
                ?: emptyList()))
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            MainMenuViewState(emptyList()),
        )

    internal fun onInteraction(interaction: MainMenuInteraction) {
        when (interaction) {
            MainMenuInteraction.OnNewGameClicked -> {
                viewModelScope.launch {
                    tresetaService.startNewGame()
                    navController.navigate(NavRoutes.TresetaGame.route)
                }
            }

            is MainMenuInteraction.TapOnDeleteButton -> {
                viewModelScope.launch {
                    tresetaService.deleteGame(interaction.gameId)
                }
            }

            is MainMenuInteraction.TapOnGameCard -> {
                tresetaService.setSelectedGame(interaction.gameId)
                navController.navigate(NavRoutes.TresetaGame.route)
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