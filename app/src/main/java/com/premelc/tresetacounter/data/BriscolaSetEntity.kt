package com.premelc.tresetacounter.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "briscolaSetTable")
data class BriscolaSetEntity(
    @ColumnInfo(name = "setId") @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "gameId") val gameId: Int,
    @ColumnInfo(name = "firstTeamScore") val firstTeamScore: Int,
    @ColumnInfo(name = "secondTeamScore") val secondTeamScore: Int,
)
