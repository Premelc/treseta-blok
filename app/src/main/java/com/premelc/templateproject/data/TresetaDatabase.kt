package com.premelc.templateproject.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        GameEntity::class,
        RoundEntity::class,
        SetEntity::class,
        CallsEntity::class,
    ],
    version = 14
)

@TypeConverters(GameTypeConverter::class, RoundTypeConverter::class)
abstract class TresetaDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun roundDao(): RoundDao
    abstract fun setDao(): SetDao
    abstract fun callsDao(): CallsDao
}