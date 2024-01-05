package com.premelc.templateproject.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "setTable")
data class SetEntity(
    @ColumnInfo(name = "setId") @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "gameId") val gameId: Int,
)
