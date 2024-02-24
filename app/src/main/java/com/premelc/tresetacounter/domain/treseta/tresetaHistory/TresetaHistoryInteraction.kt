package com.premelc.tresetacounter.domain.treseta.tresetaHistory

sealed interface TresetaHistoryInteraction {
    data object OnBackButtonClicked : TresetaHistoryInteraction
}
