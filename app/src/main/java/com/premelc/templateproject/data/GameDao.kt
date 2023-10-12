package com.premelc.templateproject.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: List<GameEntity>)

    @Query("SELECT * FROM game")
    fun getAllGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM game WHERE id IS :id")
    fun getSingleGame(id: Int): Flow<GameEntity>

    @Query("SELECT * FROM game WHERE id IS :id")
    suspend fun getCurrentGame(id: Int): GameEntity

    @Query("SELECT * FROM game ORDER BY id DESC LIMIT 1")
    suspend fun getNewGame(): GameEntity

    @Query("DELETE FROM game WHERE id = :gameId")
    suspend fun deleteGameById(gameId: Int)


}