package com.premelc.tresetacounter.domain.mainMenu

import com.premelc.tresetacounter.utils.GameType

internal sealed interface MainMenuInteraction {
    data class OnNewGameClicked(val gameTypeSelected: GameType) : MainMenuInteraction
    data class TapOnDeleteButton(val gameId: Int, val gameType: GameType) : MainMenuInteraction
    data class TapOnGameCard(val gameId: Int, val gameType: GameType) : MainMenuInteraction
    data class TapOnFavoriteButton(val gameId: Int, val gameType: GameType) : MainMenuInteraction
    data class TapOnLanguageItem(val languageCode: String) : MainMenuInteraction
    data class TapOnDialogConfirm(val gameId: Int, val gameType: GameType) : MainMenuInteraction
    data object TapOnDialogCancel : MainMenuInteraction
    data class TapOnGameTypeTab(val tabIndex: Int) : MainMenuInteraction
    data object DissmissTooManyGamesSnackbar : MainMenuInteraction
}
