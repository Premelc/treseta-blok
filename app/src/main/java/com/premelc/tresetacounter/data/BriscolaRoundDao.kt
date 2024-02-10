package com.premelc.tresetacounter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BriscolaRoundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: BriscolaRoundEntity): Long

    @Query("SELECT * FROM briscolaRound WHERE setId  IS :setId")
    suspend fun getAllRounds(setId: Int): List<BriscolaRoundEntity>

    @Query("SELECT * FROM briscolaRound")
    fun getRounds(): Flow<List<BriscolaRoundEntity>>

    @Query("DELETE FROM briscolaRound WHERE setId IS :setId")
    suspend fun deleteRounds(setId: Int)

    @Query("DELETE FROM briscolaRound WHERE roundId IS :roundId")
    suspend fun deleteSingleRound(roundId: Int)

    @Query("SELECT * FROM briscolaRound WHERE roundId IS :roundId")
    suspend fun getSingleRound(roundId: Int): BriscolaRoundEntity
}