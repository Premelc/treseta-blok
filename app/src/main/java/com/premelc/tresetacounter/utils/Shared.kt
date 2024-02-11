package com.premelc.tresetacounter.utils

import com.premelc.tresetacounter.service.data.Round

enum class Team {
    FIRST,
    SECOND,
    NONE,
}

enum class Call(val value: Int) {
    NAPOLITANA(3),
    X3(3),
    X4(4),
}

enum class GameType{
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