package com.premelc.tresetacounter.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.premelc.tresetacounter.utils.Call
import com.premelc.tresetacounter.utils.Team

@Entity(tableName = "call")
class CallsEntity(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "roundId") val roundId: Int,
    @ColumnInfo(name = "team") val team: Team,
    @ColumnInfo(name = "call") val call: Call,
)

