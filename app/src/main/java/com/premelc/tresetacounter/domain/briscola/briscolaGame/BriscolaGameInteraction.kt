package com.premelc.tresetacounter.domain.briscola.briscolaGame

import com.premelc.tresetacounter.utils.Team

internal sealed interface BriscolaGameInteraction {
    data class TapOnAddPointButton(val team: Team) : BriscolaGameInteraction
    data object TapOnBackButton : BriscolaGameInteraction
    data object TapOnMenuButton : BriscolaGameInteraction
    data object TapOnSetFinishedModalConfirm : BriscolaGameInteraction
}