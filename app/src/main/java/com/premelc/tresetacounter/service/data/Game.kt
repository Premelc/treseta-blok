package com.premelc.tresetacounter.service.data

import com.premelc.tresetacounter.domain.gameCalculator.Call

sealed interface GameState{
    data class GameReady(
        val gameId: Int,
        val isFavorite: Boolean,
        val firstTeamScore: Int,
        val secondTeamScore: Int,
        val setList: List<GameSet>
    ):GameState

    data object NoActiveGames: GameState
}

data class GameSet(
    val id: Int,
    val roundsList: List<Round>
)

data class Round(
    val id: Int,
    val timestamp: Long,
    val firstTeamPoints: Int,
    val secondTeamPoints: Int,
    val firstTeamCalls: List<Call>,
    val secondTeamCalls: List<Call>,
)
