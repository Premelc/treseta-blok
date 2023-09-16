package com.premelc.templateproject.domain.tresetaGame

import com.premelc.templateproject.data.RoundEntity
import com.premelc.templateproject.domain.gameCalculator.Team

data class TresetaGameViewState(
    val gameId: Int = 0,
    val rounds: List<RoundEntity> = emptyList(),
    val firstTeamScore: Int = 0,
    val secondTeamScore: Int = 0,
    val winningTeam: Team = Team.NONE,
)