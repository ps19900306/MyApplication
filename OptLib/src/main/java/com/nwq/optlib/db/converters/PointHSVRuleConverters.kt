package com.nwq.optlib.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nwq.optlib.bean.PointHSVRule


class PointHSVRuleConverters {

    // 将 List<KeyPoint> 转换为 JSON 字符串
    @TypeConverter
    fun fromPointHSVRuleList(list: List<PointHSVRule>): String {
        return Gson().toJson(list)
    }

    // 将 JSON 字符串转换回 List<KeyPoint>
    @TypeConverter
    fun toPointHSVRuleList(str: String): List<PointHSVRule> {
        val keyPointType = object : TypeToken<List<PointHSVRule>>() {}.type
        return Gson().fromJson(str, keyPointType)
    }


}