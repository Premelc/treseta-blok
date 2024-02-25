package com.premelc.tresetacounter.domain.mainMenu

import com.premelc.tresetacounter.data.GameEntity

data class MainMenuViewState(
    val tresetaGames: List<GameEntity> = emptyList(),
    val briscolaGames: List<GameEntity> = emptyList(),
    val selectedLanguage: String = "en",
)
