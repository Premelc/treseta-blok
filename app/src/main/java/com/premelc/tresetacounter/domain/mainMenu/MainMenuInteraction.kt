package com.premelc.tresetacounter.domain.mainMenu

internal sealed interface MainMenuInteraction {
    data object OnNewGameClicked : MainMenuInteraction
    data class TapOnDeleteButton(val gameId: Int) : MainMenuInteraction
    data class TapOnGameCard(val gameId: Int) : MainMenuInteraction
    data class TapOnFavoriteButton(val gameId: Int) : MainMenuInteraction
}