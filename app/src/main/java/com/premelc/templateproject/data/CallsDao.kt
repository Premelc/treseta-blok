package com.premelc.templateproject.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface CallsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalls(calls : List<CallsEntity>)
}