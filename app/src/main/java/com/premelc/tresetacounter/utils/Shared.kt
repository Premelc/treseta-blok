package com.premelc.tresetacounter.utils

import com.premelc.tresetacounter.service.data.Round

private const val NAPOLITANA_POINTS = 3
private const val X3_POINTS = 3
private const val X4_POINTS = 4

enum class Team {
    FIRST,
    SECOND,
    NONE,
}

enum class Call(val value: Int) {
    NAPOLITANA(NAPOLITANA_POINTS),
    X3(X3_POINTS),
    X4(X4_POINTS),
}

enum class GameType {
    TRESETA,
    BRISCOLA,
}

fun List<Round>.checkWinningTeam(): Team {
    val firstTeamPoints = this.sumOf { it.firstTeamPoints }
    val secondTeamPoints = this.sumOf { it.secondTeamPoints }
    return when {
        firstTeamPoints > secondTeamPoints -> Team.FIRST
        secondTeamPoints > firstTeamPoints -> Team.SECOND
        else -> Team.NONE
    }
}
