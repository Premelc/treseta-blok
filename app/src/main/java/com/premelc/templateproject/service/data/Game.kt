package com.premelc.templateproject.service.data

sealed interface GameState{
    data class GameReady(
        val gameId: Int,
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
    val firstTeamPoints: Int,
    val secondTeamPoints: Int,
)
