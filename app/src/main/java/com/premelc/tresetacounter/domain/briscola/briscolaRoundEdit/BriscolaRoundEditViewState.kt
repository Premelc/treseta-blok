package com.premelc.tresetacounter.domain.briscola.briscolaRoundEdit

import com.premelc.tresetacounter.utils.Team

internal data class BriscolaRoundEditViewState(
    val newRoundData: BriscolaRoundData = BriscolaRoundData(),
    val oldRoundData: BriscolaRoundData = BriscolaRoundData(),
    val selectedTeam: Team? = null,
    val isSaveButtonEnabled: Boolean = false,
    val showDeleteRoundDialog: Boolean = false,
)

data class BriscolaRoundData(
    val firstTeamScore: Int? = null,
    val secondTeamScore: Int? = null,
)
