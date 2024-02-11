package com.premelc.tresetacounter.domain.briscola.briscolaGame

import com.premelc.tresetacounter.service.data.Round
import com.premelc.tresetacounter.utils.Team

internal sealed interface BriscolaGameViewState {
    data class GameReady(
        val gameId: Int = 0,
        val rounds: List<Round> = emptyList(),
        val firstTeamScore: Int = 0,
        val secondTeamScore: Int = 0,
        val winningTeam: Team = Team.NONE,
        val showHistoryButton: Boolean = false,
        val currentSetId: Int = 0,
        val showSetFinishedModal: Boolean = false,
    ) : BriscolaGameViewState

    data object GameLoading : BriscolaGameViewState
}
