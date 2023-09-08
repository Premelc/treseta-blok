package com.premelc.templateproject.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.StateFlow

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGame(game: List<GameEntity>)

    @Query("SELECT * FROM game")
    fun getAllGames(): List<GameEntity>

    @Query("SELECT * FROM game WHERE id IS :id")
    fun getSingleGame(id: Int): GameEntity

}