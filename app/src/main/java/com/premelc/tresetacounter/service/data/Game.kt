package com.premelc.tresetacounter.service.data

import com.premelc.tresetacounter.utils.Call

sealed interface GameState {
    data class GameReady(
        val gameId: Int,
        val isFavorite: Boolean,
        val firstTeamScore: Int,
        val secondTeamScore: Int,
        val setList: List<GameSet>,
    ) : GameState

    data object NoActiveGames : GameState
}

data class GameSet(
    val id: Int,
    val roundsList: List<Round>
)

abstract class Round(
    open val id: Int,
    open val setId: Int,
    open val timestamp: Long,
    open val firstTeamPoints: Int,
    open val secondTeamPoints: Int,
)

data class TresetaRound(
    override val id: Int,
    override val setId: Int,
    override val timestamp: Long,
    override val firstTeamPoints: Int,
    override val secondTeamPoints: Int,
    val firstTeamPointsNoCalls: Int,
    val secondTeamPointsNoCalls: Int,
    val firstTeamCalls: List<Call>,
    val secondTeamCalls: List<Call>,
) : Round(
    id = id,
    setId = setId,
    timestamp = timestamp,
    firstTeamPoints = firstTeamPoints,
    secondTeamPoints = secondTeamPoints
)

data class BriscolaRound(
    override val id: Int,
    override val setId: Int,
    override val timestamp: Long,
    override val firstTeamPoints: Int,
    override val secondTeamPoints: Int,
    val firstTeamPointsCollected: Int,
    val secondTeamPointsCollected: Int,
) : Round(
    id = id,
    setId = setId,
    timestamp = timestamp,
    firstTeamPoints = firstTeamPoints,
    secondTeamPoints = secondTeamPoints
)
