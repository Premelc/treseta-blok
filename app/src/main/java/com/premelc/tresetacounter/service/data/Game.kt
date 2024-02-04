package com.premelc.tresetacounter.service.data

import com.premelc.tresetacounter.utils.Call

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
    val setId: Int,
    val timestamp: Long,
    val firstTeamPoints: Int,
    val firstTeamPointsNoCalls: Int,
    val secondTeamPoints: Int,
    val secondTeamPointsNoCalls: Int,
    val firstTeamCalls: List<Call>,
    val secondTeamCalls: List<Call>,
)
