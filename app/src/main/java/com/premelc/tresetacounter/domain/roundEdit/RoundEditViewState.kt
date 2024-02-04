package com.premelc.tresetacounter.domain.roundEdit

import com.premelc.tresetacounter.utils.Call
import com.premelc.tresetacounter.utils.Team

internal data class RoundEditViewState(
    val newRoundData: RoundData = RoundData(),
    val oldRoundData: RoundData = RoundData(),
    val selectedTeam: Team? = null,
    val isSaveButtonEnabled: Boolean = false,
    val showDeleteRoundDialog: Boolean = false,
)

data class RoundData(
    val firstTeamScore: Int? = null,
    val firstTeamCalls: List<Call> = emptyList(),
    val secondTeamScore: Int? = null,
    val secondTeamCalls: List<Call> = emptyList(),
)
