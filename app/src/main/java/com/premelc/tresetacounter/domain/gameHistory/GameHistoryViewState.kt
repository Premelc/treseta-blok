package com.premelc.tresetacounter.domain.gameHistory

import com.premelc.tresetacounter.service.data.GameSet

data class GameHistoryViewState(
    val firstTeamScore: Int = 0,
    val secondTeamScore: Int = 0,
    val sets: List<GameSet> = emptyList()
)