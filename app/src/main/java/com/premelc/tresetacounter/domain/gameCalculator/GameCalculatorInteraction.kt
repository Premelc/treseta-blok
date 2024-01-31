package com.premelc.tresetacounter.domain.gameCalculator

internal sealed interface GameCalculatorInteraction {
    data class TapOnCallButton(val call: Call) : GameCalculatorInteraction
    data class TapOnNumberButton(val input: Int) : GameCalculatorInteraction
    data object TapOnSaveButton : GameCalculatorInteraction
    data object TapOnDeleteButton : GameCalculatorInteraction
    data class TapOnTeamCard(val team: Team) : GameCalculatorInteraction
    data class TapOnRemovablePill(val index: Int, val team: Team) : GameCalculatorInteraction
    data object TapOnBackButton : GameCalculatorInteraction
}