package com.premelc.tresetacounter.domain.briscola.briscolaGame

internal sealed interface BriscolaGameInteraction {
    data object TapOnNewRound : BriscolaGameInteraction
    data object TapOnBackButton : BriscolaGameInteraction
    data object TapOnHistoryButton : BriscolaGameInteraction
    data object TapOnMenuButton : BriscolaGameInteraction
    data class TapOnRoundScore(val roundId: Int) : BriscolaGameInteraction
}