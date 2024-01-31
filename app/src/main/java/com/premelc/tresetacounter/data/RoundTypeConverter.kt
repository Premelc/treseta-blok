package com.premelc.tresetacounter.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoundTypeConverter {
    @TypeConverter
    fun gameToString(round: RoundEntity): String {
        return Gson().toJson(round)
    }

    @TypeConverter
    fun stringToRecipe(roundString: String): RoundEntity {
        val objectType = object : TypeToken<RoundEntity>() {}.type
        return Gson().fromJson(roundString, objectType)
    }
}