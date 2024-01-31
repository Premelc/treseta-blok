package com.premelc.tresetacounter.domain.tresetaGame

internal sealed interface TresetaGameInteraction {
    data object TapOnNewRound : TresetaGameInteraction
    data object TapOnBackButton : TresetaGameInteraction
    data object TapOnHistoryButton : TresetaGameInteraction
    data object TapOnMenuButton : TresetaGameInteraction
}