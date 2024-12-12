package com.nwq.opencv.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nwq.opencv.rgb.PointRule



class PointRuleConverters {

    // 将 List<KeyPoint> 转换为 JSON 字符串
    @TypeConverter
    fun fromPointRuleList(list: List<PointRule>): String {
        return Gson().toJson(list)
    }

    // 将 JSON 字符串转换回 List<KeyPoint>
    @TypeConverter
    fun toPointRuleList(str: String): List<PointRule> {
        val keyPointType = object : TypeToken<List<PointRule>>() {}.type
        return Gson().fromJson(str, keyPointType)
    }


}