package com.premelc.tresetacounter.domain.briscola.briscolaCalculator

import com.premelc.tresetacounter.utils.Team

internal sealed interface BriscolaCalculatorInteraction {
    data class TapOnTeamCard(val team: Team) : BriscolaCalculatorInteraction
    data object TapOnBackButton : BriscolaCalculatorInteraction
}