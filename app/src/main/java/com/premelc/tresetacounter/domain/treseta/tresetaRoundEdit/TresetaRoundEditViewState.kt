package com.premelc.tresetacounter.domain.treseta.tresetaRoundEdit

import com.premelc.tresetacounter.utils.Call
import com.premelc.tresetacounter.utils.Team

internal data class RoundEditViewState(
    val newRoundData: TresetaRoundData = TresetaRoundData(),
    val oldRoundData: TresetaRoundData = TresetaRoundData(),
    val selectedTeam: Team? = null,
    val isSaveButtonEnabled: Boolean = false,
    val showDeleteRoundDialog: Boolean = false,
)

data class TresetaRoundData(
    val firstTeamScore: Int? = null,
    val firstTeamCalls: List<Call> = emptyList(),
    val secondTeamScore: Int? = null,
    val secondTeamCalls: List<Call> = emptyList(),
)
