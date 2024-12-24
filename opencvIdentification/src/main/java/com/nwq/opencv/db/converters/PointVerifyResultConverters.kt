package com.nwq.opencv.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nwq.opencv.data.PointVerifyResult
import com.nwq.opencv.hsv.PointHSVRule
import org.opencv.core.Point


class PointVerifyResultConverters {

    // 将 List<KeyPoint> 转换为 JSON 字符串
    @TypeConverter
    fun fromPointHSVRuleList(list: List<PointVerifyResult>): String {
        return Gson().toJson(list)
    }

    // 将 JSON 字符串转换回 List<PointVerifyResult>
    @TypeConverter
    fun toPointHSVRuleList(str: String): List<PointVerifyResult> {
        val keyPointType = object : TypeToken<List<PointVerifyResult>>() {}.type
        return Gson().fromJson(str, keyPointType)
    }


}