package com.nwq.opencv.db.converters

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import org.opencv.core.KeyPoint
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class KeyPointConverters{

    // 将 List<KeyPoint> 转换为 JSON 字符串
    @TypeConverter
    fun fromKeyPointList(list: List<KeyPoint>): String {
        return Gson().toJson(list)
    }

    // 将 JSON 字符串转换回 List<KeyPoint>
    @TypeConverter
    fun toKeyPointList(str: String): List<KeyPoint> {
        val keyPointType = object : TypeToken<List<KeyPoint>>() {}.type
        return Gson().fromJson(str, keyPointType)
    }


}