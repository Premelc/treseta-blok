package com.premelc.tresetacounter.domain.treseta.tresetaCalculator

import com.premelc.tresetacounter.utils.Call
import com.premelc.tresetacounter.utils.Team

internal sealed interface TresetaCalculatorInteraction {
    data class TapOnCallButton(val call: Call) : TresetaCalculatorInteraction
    data class TapOnTeamCard(val team: Team) : TresetaCalculatorInteraction
    data class TapOnRemovablePill(val index: Int, val team: Team) : TresetaCalculatorInteraction
    data object TapOnBackButton : TresetaCalculatorInteraction
}