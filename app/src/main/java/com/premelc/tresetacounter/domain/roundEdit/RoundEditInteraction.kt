package com.premelc.tresetacounter.domain.roundEdit

import com.premelc.tresetacounter.utils.Call
import com.premelc.tresetacounter.utils.Team

internal sealed interface RoundEditInteraction {
    data class TapOnCallButton(val call: Call) : RoundEditInteraction
    data class TapOnTeamCard(val team: Team) : RoundEditInteraction
    data class TapOnRemovablePill(val index: Int, val team: Team) : RoundEditInteraction
    data object TapOnBackButton : RoundEditInteraction
    data object TapOnDeleteRound : RoundEditInteraction
    data object TapOnDeleteRoundDialogPositive : RoundEditInteraction
    data object TapOnDeleteRoundDialogNegative : RoundEditInteraction
}