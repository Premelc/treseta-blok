package com.premelc.tresetacounter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: List<SetEntity>)

    @Query("SELECT * FROM setTable WHERE gameId IS :gameId ")
    fun getAllSets(gameId: Int): Flow<List<SetEntity>>

    @Query("SELECT * FROM setTable WHERE setId IS :id")
    fun getSingleSet(id: Int): Flow<SetEntity>

    @Query("SELECT * FROM setTable WHERE gameId IS :gameId ORDER BY setId DESC LIMIT 1")
    suspend fun getLatestSet(gameId: Int): SetEntity

    @Query("DELETE FROM setTable WHERE gameID IS :gameId")
    suspend fun deleteSets(gameId: Int)
}
