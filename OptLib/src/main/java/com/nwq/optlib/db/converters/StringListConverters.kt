package com.nwq.optlib.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.opencv.core.Point


class StringListConverters {

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return Gson().toJson(list)
    }


    @TypeConverter
    fun toStringList(str: String): List<String> {
        val keyPointType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(str, keyPointType)
    }


}