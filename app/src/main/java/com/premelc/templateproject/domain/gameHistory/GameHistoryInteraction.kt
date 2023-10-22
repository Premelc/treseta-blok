package com.premelc.templateproject.domain.gameHistory

sealed interface GameHistoryInteraction {
    data object OnBackButtonClicked: GameHistoryInteraction
}