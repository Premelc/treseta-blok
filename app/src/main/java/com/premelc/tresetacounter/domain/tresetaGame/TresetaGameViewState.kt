package com.premelc.tresetacounter.domain.tresetaGame

import com.premelc.tresetacounter.data.RoundEntity
import com.premelc.tresetacounter.domain.gameCalculator.Team
import com.premelc.tresetacounter.service.data.Round


internal sealed interface TresetaGameViewState {
    data class GameReady(
        val gameId: Int = 0,
        val rounds: List<Round> = emptyList(),
        val firstTeamScore: Int = 0,
        val secondTeamScore: Int = 0,
        val winningTeam: Team = Team.NONE,
        val showHistoryButton: Boolean = false,
        val currentSetId: Int = 0,
    ) : TresetaGameViewState

    data object GameLoading : TresetaGameViewState
}
