package com.premelc.tresetacounter.domain.treseta.tresetaHistory

import com.premelc.tresetacounter.service.data.GameSet

data class TresetaHistoryViewState(
    val firstTeamScore: Int = 0,
    val secondTeamScore: Int = 0,
    val sets: List<GameSet> = emptyList()
)