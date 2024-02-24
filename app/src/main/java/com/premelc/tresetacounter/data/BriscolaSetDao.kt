package com.premelc.tresetacounter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BriscolaSetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: List<BriscolaSetEntity>)

    @Query("SELECT * FROM briscolaSetTable WHERE gameId IS :gameId ")
    fun getAllSets(gameId: Int): Flow<List<BriscolaSetEntity>>

    @Query("SELECT * FROM briscolaSetTable WHERE setId IS :id")
    fun getSingleSet(id: Int): Flow<BriscolaSetEntity>

    @Query("SELECT * FROM briscolaSetTable WHERE gameId IS :gameId ORDER BY setId DESC LIMIT 1")
    suspend fun getLatestSet(gameId: Int): BriscolaSetEntity

    @Query("DELETE FROM briscolaSetTable WHERE gameID IS :gameId")
    suspend fun deleteSets(gameId: Int)
}
