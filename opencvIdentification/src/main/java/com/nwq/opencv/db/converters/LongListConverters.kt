package com.nwq.opencv.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.opencv.core.Point


class LongListConverters {

    // 将 List<KeyPoint> 转换为 JSON 字符串
    @TypeConverter
    fun fromLongList(list: List<Long>): String {
        return Gson().toJson(list)
    }

    // 将 JSON 字符串转换回 List<KeyPoint>
    @TypeConverter
    fun toLongList(str: String): List<Long> {
        val keyPointType = object : TypeToken<List<Long>>() {}.type
        return Gson().fromJson(str, keyPointType)
    }


}