package com.premelc.templateproject.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SetRoundDao {
    @Query("SELECT * from setTable WHERE setId IS :setId")
    fun getSetAndRounds(setId:Int): List<SetAndRounds?>?
}