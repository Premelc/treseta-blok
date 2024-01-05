package com.premelc.templateproject.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "round")
class RoundEntity(
    @ColumnInfo(name = "roundId") @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "setId") val setId: Int,
    @ColumnInfo(name = "firstTeamPoints") val firstTeamPoints: Int,
    @ColumnInfo(name = "secondTeamPoints") val secondTeamPoints: Int,
)

