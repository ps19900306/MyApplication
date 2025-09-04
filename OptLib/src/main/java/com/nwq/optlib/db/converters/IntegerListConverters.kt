package com.nwq.optlib.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.opencv.core.Point


class IntegerListConverters {

    @TypeConverter
    fun fromStringList(list: List<Int>): String {
        return Gson().toJson(list)
    }


    @TypeConverter
    fun toLongList(str: String): List<Long> {
        val keyPointType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(str, keyPointType)
    }


}