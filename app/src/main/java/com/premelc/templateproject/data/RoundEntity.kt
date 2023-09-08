package com.premelc.templateproject.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "round")
class RoundEntity(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "gameId") val gameId: String,
    @ColumnInfo(name = "firstTeamPoints") val firstTeamPoints: Int,
    @ColumnInfo(name = "secondTeamPoints") val secondTeamPoints: Int,
)