package com.nwq.opencv.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nwq.baseutils.GsonUtils
import com.nwq.opencv.hsv.HSVRule
import com.nwq.opencv.hsv.PointHSVRule
import org.opencv.core.Point


class HSVRuleConverters {

    // 将 List<KeyPoint> 转换为 JSON 字符串
    @TypeConverter
    fun fromHSVRuleList(list: List<HSVRule>): String {
        return GsonUtils.toJson(list)
    }

    // 将 JSON 字符串转换回 List<KeyPoint>
    @TypeConverter
    fun toHSVRuleList(str: String): List<HSVRule> {
        val keyPointType = object : TypeToken<List<HSVRule>>() {}.type
        return Gson().fromJson(str, keyPointType)
    }


}