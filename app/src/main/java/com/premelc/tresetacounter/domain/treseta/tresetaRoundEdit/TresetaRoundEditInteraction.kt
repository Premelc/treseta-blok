package com.premelc.tresetacounter.domain.treseta.tresetaRoundEdit

import com.premelc.tresetacounter.utils.Call
import com.premelc.tresetacounter.utils.Team

internal sealed interface TresetaRoundEditInteraction {
    data class TapOnCallButton(val call: Call) : TresetaRoundEditInteraction
    data class TapOnTeamCard(val team: Team) : TresetaRoundEditInteraction
    data class TapOnRemovablePill(val index: Int, val team: Team) : TresetaRoundEditInteraction
    data object TapOnBackButton : TresetaRoundEditInteraction
    data object TapOnDeleteRound : TresetaRoundEditInteraction
    data object TapOnDeleteRoundDialogPositive : TresetaRoundEditInteraction
    data object TapOnDeleteRoundDialogNegative : TresetaRoundEditInteraction
    data object DismissCallsSnackbar : TresetaRoundEditInteraction
}
