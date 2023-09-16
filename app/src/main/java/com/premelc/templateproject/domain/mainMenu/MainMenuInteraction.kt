package com.premelc.templateproject.domain.mainMenu

internal sealed interface MainMenuInteraction {
    data object OnNewGameClicked : MainMenuInteraction
    data object TestButton : MainMenuInteraction
    data class TapOnDeleteButton(val gameId: Int) : MainMenuInteraction
    data class TapOnGameCard(val gameId: Int) : MainMenuInteraction
}