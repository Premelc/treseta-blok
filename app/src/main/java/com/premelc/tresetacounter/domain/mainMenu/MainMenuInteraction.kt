package com.premelc.tresetacounter.domain.mainMenu

import com.premelc.tresetacounter.utils.GameType

internal sealed interface MainMenuInteraction {
    data class OnNewGameClicked(val gameTypeSelected: GameType) : MainMenuInteraction
    data class TapOnDeleteButton(val gameId: Int, val gameType: GameType) : MainMenuInteraction
    data class TapOnGameCard(val gameId: Int, val gameType: GameType) : MainMenuInteraction
    data class TapOnFavoriteButton(val gameId: Int, val gameType: GameType) : MainMenuInteraction
}
