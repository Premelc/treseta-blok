package com.premelc.templateproject.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.premelc.templateproject.domain.gameCalculator.Call
import com.premelc.templateproject.domain.gameCalculator.Team

@Entity(tableName = "call")
class CallsEntity(
    @ColumnInfo(name = "callId") @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "roundId") val roundId: Int,
    @ColumnInfo(name = "team") val team: Team,
    @ColumnInfo(name = "call") val call: Call,
)

