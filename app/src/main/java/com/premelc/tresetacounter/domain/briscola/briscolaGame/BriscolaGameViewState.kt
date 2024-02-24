package com.premelc.tresetacounter.domain.briscola.briscolaGame

import com.premelc.tresetacounter.utils.Team

internal sealed interface BriscolaGameViewState {
    data class GameReady(
        val gameId: Int = 0,
        val firstTeamScore: Int = 0,
        val firstTeamCurrentSetScore: Int = 0,
        val secondTeamScore: Int = 0,
        val secondTeamCurrentSetScore: Int = 0,
        val winningTeam: Team = Team.NONE,
        val currentSetId: Int = 0,
        val showSetFinishedModal: Boolean = false,
    ) : BriscolaGameViewState

    data object GameLoading : BriscolaGameViewState
}
