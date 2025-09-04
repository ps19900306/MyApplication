package com.nwq.optlib.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.opencv.core.Point


class LongListConverters {

    @TypeConverter
    fun fromStringList(list: List<Long>): String {
        return Gson().toJson(list)
    }


    @TypeConverter
    fun toLongList(str: String): List<Long> {
        val keyPointType = object : TypeToken<List<Long>>() {}.type
        return Gson().fromJson(str, keyPointType)
    }


}