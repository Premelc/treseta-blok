package com.premelc.tresetacounter.service.data

import com.premelc.tresetacounter.utils.Call

sealed interface TresetaGameState {
    data class GameReady(
        val gameId: Int,
        val isFavorite: Boolean,
        val firstTeamScore: Int,
        val secondTeamScore: Int,
        val setList: List<TresetaGameSet>,
    ) : TresetaGameState

    data object NoActiveGames : TresetaGameState
}

sealed interface BriscolaGameState {
    data class GameReady(
        val gameId: Int,
        val isFavorite: Boolean,
        val firstTeamScore: Int,
        val secondTeamScore: Int,
        val setList: List<BriscolaGameSet>,
    ) : BriscolaGameState

    data object NoActiveGames : BriscolaGameState
}

data class TresetaGameSet(
    val id: Int,
    val roundsList: List<Round>,
)

data class BriscolaGameSet(
    val id: Int,
    val firstTeamPoints: Int,
    val secondTeamPoints: Int,
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
