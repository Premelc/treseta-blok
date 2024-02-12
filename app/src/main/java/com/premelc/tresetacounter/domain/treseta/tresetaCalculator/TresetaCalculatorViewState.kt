package com.premelc.tresetacounter.domain.treseta.tresetaCalculator

import com.premelc.tresetacounter.utils.Call
import com.premelc.tresetacounter.utils.Team

internal data class TresetaCalculatorViewState(
    val firstTeamScore: Int? = null,
    val firstTeamCalls: List<Call> = emptyList(),
    val secondTeamScore: Int? = null,
    val secondTeamCalls: List<Call> = emptyList(),
    val selectedTeam: Team? = null,
    val isSaveButtonEnabled: Boolean = false,
    val showCallsSnackbar: Boolean = false,
)
