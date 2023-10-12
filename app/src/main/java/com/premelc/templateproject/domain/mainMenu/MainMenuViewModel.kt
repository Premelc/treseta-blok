package com.premelc.templateproject.domain.mainMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.premelc.templateproject.data.GameEntity
import com.premelc.templateproject.data.SetEntity
import com.premelc.templateproject.data.TresetaDatabase
import com.premelc.templateproject.navigation.NavRoutes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainMenuViewModel(
    private val tresetaDatabase: TresetaDatabase,
    private val navController: NavController,
) : ViewModel() {



    val viewState =
        tresetaDatabase.gameDao().getAllGames().flatMapLatest {
            MutableStateFlow(MainMenuViewState(it))
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            MainMenuViewState(emptyList()),
        )





    internal fun onInteraction(interaction: MainMenuInteraction) {
        when (interaction) {
            MainMenuInteraction.OnNewGameClicked -> {
                viewModelScope.launch {
                    val newGameId =
                        addNewGame(GameEntity(id = 0, firstTeamPoints = 0, secondTeamPoints = 0))
                    navController.navigate(NavRoutes.TresetaGame.route.plus("/${newGameId}"))
                }
            }

            MainMenuInteraction.TestButton -> {
                viewModelScope.launch {
                    tresetaDatabase.gameDao().getNewGame()
                }
            }

            is MainMenuInteraction.TapOnDeleteButton -> {
                viewModelScope.launch {
                    tresetaDatabase.gameDao().deleteGameById(interaction.gameId)
                }
            }

            is MainMenuInteraction.TapOnGameCard -> {
                navController.navigate(NavRoutes.TresetaGame.route.plus("/${interaction.gameId}"))
            }
        }
    }

    private suspend fun addNewGame(game: GameEntity): Int {
        tresetaDatabase.gameDao().insertGame(listOf(game))
        val newGameId = tresetaDatabase.gameDao().getNewGame().id
        tresetaDatabase.setDao().insertSet(listOf(SetEntity(id = 0, gameId = newGameId)))
        return newGameId
    }
}