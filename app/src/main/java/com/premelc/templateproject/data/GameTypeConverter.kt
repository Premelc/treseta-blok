package com.premelc.templateproject.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GameTypeConverter {
    @TypeConverter
    fun gameToString(recipe: GameEntity): String {
        return Gson().toJson(recipe)
    }

    @TypeConverter
    fun stringToRecipe(gameString: String): GameEntity {
        val objectType = object : TypeToken<GameEntity>() {}.type
        return Gson().fromJson(gameString, objectType)
    }
}