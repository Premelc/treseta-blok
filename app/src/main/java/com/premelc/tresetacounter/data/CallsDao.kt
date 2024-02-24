package com.premelc.tresetacounter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CallsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalls(calls: List<CallsEntity>)

    @Query("SELECT * FROM call WHERE roundId IS :roundId")
    suspend fun getCallsInRound(roundId: Int): List<CallsEntity>

    @Query("DELETE FROM call where roundId is :roundId")
    suspend fun deleteCallsFromRound(roundId: Int)
}
