package com.premelc.templateproject.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.premelc.templateproject.service.data.Round


@Entity(tableName = "setTable")
data class SetEntity(
    @ColumnInfo(name = "setId") @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "gameId") val gameId: Int,
)

@Entity(tableName = "round")
class RoundEntity(
    @ColumnInfo(name = "roundId") @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "setId") val setId: Int,
    @ColumnInfo(name = "firstTeamPoints") val firstTeamPoints: Int,
    @ColumnInfo(name = "secondTeamPoints") val secondTeamPoints: Int,
)

class SetAndRounds {
    @Embedded
    var set: SetEntity? = null

    @Relation(parentColumn = "setId" , entityColumn = "roundId")
    var roundsList: List<RoundEntity>? = null
}

