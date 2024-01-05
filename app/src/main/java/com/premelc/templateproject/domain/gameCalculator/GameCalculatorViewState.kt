package com.premelc.templateproject.domain.gameCalculator

internal data class GameCalculatorViewState(
    val firstTeamScore: Int? = null,
    val firstTeamCalls: List<Call> = emptyList(),
    val secondTeamScore: Int? = null,
    val secondTeamCalls: List<Call> = emptyList(),
    val selectedTeam: Team? = null,
    val isSaveButtonEnabled: Boolean = false,
)

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
