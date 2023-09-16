package com.premelc.templateproject.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RoundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: List<RoundEntity>)

    @Query("SELECT * FROM round WHERE gameId is :gameId")
    fun getAllRounds(gameId: Int): Flow<List<RoundEntity>>
}