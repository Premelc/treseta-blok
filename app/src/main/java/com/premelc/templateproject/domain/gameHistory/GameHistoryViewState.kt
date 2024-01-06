package com.premelc.templateproject.domain.gameHistory

import com.premelc.templateproject.service.data.GameSet

data class GameHistoryViewState(
    val firstTeamScore: Int = 0,
    val secondTeamScore: Int = 0,
    val sets: List<GameSet> = emptyList()
)