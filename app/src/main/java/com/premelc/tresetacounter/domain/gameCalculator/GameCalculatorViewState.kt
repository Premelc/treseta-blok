package com.premelc.tresetacounter.domain.gameCalculator

import com.premelc.tresetacounter.utils.Call
import com.premelc.tresetacounter.utils.Team

internal data class GameCalculatorViewState(
    val firstTeamScore: Int? = null,
    val firstTeamCalls: List<Call> = emptyList(),
    val secondTeamScore: Int? = null,
    val secondTeamCalls: List<Call> = emptyList(),
    val selectedTeam: Team? = null,
    val isSaveButtonEnabled: Boolean = false,
)
