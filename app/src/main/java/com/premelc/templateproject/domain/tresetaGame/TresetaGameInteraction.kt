package com.premelc.templateproject.domain.tresetaGame

internal sealed interface TresetaGameInteraction {
    data object TapOnNewRound : TresetaGameInteraction
    data object TapOnBackButton : TresetaGameInteraction
}