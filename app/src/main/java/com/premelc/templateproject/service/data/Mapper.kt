package com.premelc.templateproject.service.data

import com.premelc.templateproject.data.RoundEntity

fun RoundEntity.toRound() = Round(
    id = this.id,
    firstTeamPoints = this.firstTeamPoints,
    secondTeamPoints = this.secondTeamPoints
)