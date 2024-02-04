package com.premelc.tresetacounter.domain.gameCalculator

import com.premelc.tresetacounter.utils.Call
import com.premelc.tresetacounter.utils.Team

internal sealed interface GameCalculatorInteraction {
    data class TapOnCallButton(val call: Call) : GameCalculatorInteraction
    data class TapOnTeamCard(val team: Team) : GameCalculatorInteraction
    data class TapOnRemovablePill(val index: Int, val team: Team) : GameCalculatorInteraction
    data object TapOnBackButton : GameCalculatorInteraction
}