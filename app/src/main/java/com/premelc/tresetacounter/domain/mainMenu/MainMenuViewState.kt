package com.premelc.tresetacounter.domain.mainMenu

import com.premelc.tresetacounter.data.GameEntity
import com.premelc.tresetacounter.utils.GameType

data class MainMenuViewState(
    val tresetaGames: List<GameEntity> = emptyList(),
    val briscolaGames: List<GameEntity> = emptyList(),
    val selectedLanguage: String = "en",
    val showDeleteGameDialog: Pair<Int, GameType>? = null,
    val showTooManyGamesSnackbar: Boolean = false,
    val selectedGameType: Int,
)
