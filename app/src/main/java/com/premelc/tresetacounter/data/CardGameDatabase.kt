package com.premelc.tresetacounter.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        GameEntity::class,
        RoundEntity::class,
        SetEntity::class,
        CallsEntity::class,
        BriscolaRoundEntity::class,
    ],
    version = 19
)

@TypeConverters(GameTypeConverter::class, RoundTypeConverter::class)
abstract class CardGameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun roundDao(): RoundDao
    abstract fun setDao(): SetDao
    abstract fun callsDao(): CallsDao
    abstract fun briscolaRoundDao(): BriscolaRoundDao
}