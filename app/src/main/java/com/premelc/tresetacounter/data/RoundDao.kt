package com.premelc.tresetacounter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RoundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: RoundEntity): Long

    @Query("SELECT * FROM round WHERE setId  IS :setId")
    suspend fun getAllRounds(setId: Int): List<RoundEntity>

    @Query("SELECT * FROM round")
    fun getRounds(): Flow<List<RoundEntity>>

    @Query("DELETE FROM round WHERE setId IS :setId")
    suspend fun deleteRounds(setId: Int)

    @Query("DELETE FROM round WHERE roundId IS :roundId")
    suspend fun deleteSingleRound(roundId: Int)

    @Query("SELECT * FROM round WHERE roundId IS :roundId")
    suspend fun getSingleRound(roundId: Int): RoundEntity
}