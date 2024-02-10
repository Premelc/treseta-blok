package com.premelc.tresetacounter.domain.briscola.briscolaCalculator

import com.premelc.tresetacounter.utils.Call
import com.premelc.tresetacounter.utils.Team

internal data class BriscolaCalculatorViewState(
    val firstTeamScore: Int? = null,
    val secondTeamScore: Int? = null,
    val selectedTeam: Team? = null,
    val isSaveButtonEnabled: Boolean = false,
)
