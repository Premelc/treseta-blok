package com.premelc.tresetacounter.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "briscolaRound")
class BriscolaRoundEntity(
    @ColumnInfo(name = "roundId") @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "setId") val setId: Int,
    @ColumnInfo(name = "firstTeamPointsCollected") val firstTeamPointsCollected: Int,
    @ColumnInfo(name = "firstTeamPoints") val firstTeamPoints: Int,
    @ColumnInfo(name = "secondTeamPointsCollected") val secondTeamPointsCollected: Int,
    @ColumnInfo(name = "secondTeamPoints") val secondTeamPoints: Int,
)