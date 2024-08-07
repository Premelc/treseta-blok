package com.premelc.tresetacounter.domain.treseta.tresetaGame

internal sealed interface TresetaGameInteraction {
    data object TapOnNewRound : TresetaGameInteraction
    data object TapOnHistoryButton : TresetaGameInteraction
    data object TapOnMenuButton : TresetaGameInteraction
    data object TapOnSetFinishedModalConfirm : TresetaGameInteraction
    data class TapOnRoundScore(val roundId: Int) : TresetaGameInteraction
}
