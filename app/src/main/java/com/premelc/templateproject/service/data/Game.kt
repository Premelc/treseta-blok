package com.premelc.templateproject.service.data

import com.premelc.templateproject.domain.gameCalculator.Call
import java.security.Timestamp

sealed interface GameState{
    data class GameReady(
        val gameId: Int,
        val isFavorite: Boolean,
        val firstTeamScore: Int,
        val secondTeamScore: Int,
        val setList: List<GameSet>
    ):GameState
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
