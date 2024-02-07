package com.premelc.tresetacounter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.premelc.tresetacounter.utils.GameType
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: List<GameEntity>)

    @Query("SELECT * FROM game WHERE gameType IS :gameType")
    fun getAllGames(gameType: GameType): Flow<List<GameEntity>?>

    @Query("SELECT * FROM game WHERE id IS :id")
    fun getSingleGame(id: Int): Flow<GameEntity>

    @Query("SELECT * FROM game WHERE id IS :id")
    suspend fun getCurrentGame(id: Int): GameEntity

    @Query("SELECT * FROM game ORDER BY id DESC LIMIT 1")
    suspend fun getNewGame(): GameEntity

    @Query("DELETE FROM game WHERE id = :gameId")
    suspend fun deleteGameById(gameId: Int)

    @Query("SELECT * FROM game ORDER BY timestamp DESC")
    fun getLatestGame(): Flow<GameEntity?>
}