package com.premelc.tresetacounter.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        GameEntity::class,
        RoundEntity::class,
        SetEntity::class,
        CallsEntity::class,
        BriscolaSetEntity::class,
    ],
    version = 20
)
abstract class CardGameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun roundDao(): RoundDao
    abstract fun setDao(): SetDao
    abstract fun callsDao(): CallsDao
    abstract fun briscolaSetDao(): BriscolaSetDao
}
