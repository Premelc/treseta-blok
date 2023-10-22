package com.premelc.templateproject.domain.tresetaGame

import com.premelc.templateproject.data.RoundEntity
import com.premelc.templateproject.domain.gameCalculator.Team
import com.premelc.templateproject.service.data.Round

data class TresetaGameViewState(
    val gameId: Int = 0,
    val rounds: List<Round> = emptyList(),
    val firstTeamScore: Int = 0,
    val secondTeamScore: Int = 0,
    val winningTeam: Team = Team.NONE,
    val showHistoryButton: Boolean = false,
)