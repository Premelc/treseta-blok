package com.premelc.templateproject.domain.gameCalculator

internal data class GameCalculatorViewState(
    val firstTeamScore: Int? = null,
    val secondTeamScore: Int? = null,
    val selectedTeam: Team? = null,
)

internal enum class Team {
    FIRST,
    SECOND,
    NONE,
}

internal enum class Call(val value: Int) {
    NAPOLITANA(3),
    X3(3),
    X4(4),
}
