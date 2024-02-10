package com.premelc.tresetacounter.domain.briscola.briscolaRoundEdit

import com.premelc.tresetacounter.utils.Team

internal sealed interface BriscolaRoundEditInteraction {
    data class TapOnTeamCard(val team: Team) : BriscolaRoundEditInteraction
    data object TapOnBackButton : BriscolaRoundEditInteraction
    data object TapOnDeleteRound : BriscolaRoundEditInteraction
    data object TapOnDeleteRoundDialogPositive : BriscolaRoundEditInteraction
    data object TapOnDeleteRoundDialogNegative : BriscolaRoundEditInteraction
}