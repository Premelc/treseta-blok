package com.premelc.templateproject.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        GameEntity::class,
        RoundEntity::class
    ],
    version = 1
)

@TypeConverters(GameTypeConverter::class , RoundTypeConverter::class)
abstract class TresetaDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun roundDao(): RoundDao
}