package com.premelc.tresetacounter.domain.gameHistory

sealed interface GameHistoryInteraction {
    data object OnBackButtonClicked: GameHistoryInteraction
}