package com.premelc.tresetacounter.utils

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
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

internal fun List<Round>.checkWinningTeam(): Team {
    val firstTeamPoints = this.sumOf { it.firstTeamPoints }
    val secondTeamPoints = this.sumOf { it.secondTeamPoints }
    return when {
        firstTeamPoints > secondTeamPoints -> Team.FIRST
        secondTeamPoints > firstTeamPoints -> Team.SECOND
        else -> Team.NONE
    }
}

internal fun Context.changeLanguage(localeTag: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getSystemService(LocaleManager::class.java).applicationLocales =
            LocaleList.forLanguageTags(localeTag)
    } else {
        (this as Activity).recreate()
    }
}
