package com.nwq.opencv.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nwq.opencv.hsv.PointHSVRule
import org.opencv.core.Point


class PointHSVRuleConverters {

    // 将 List<KeyPoint> 转换为 JSON 字符串
    @TypeConverter
    fun fromKeyPointList(keypoints: List<PointHSVRule>): String {
        return Gson().toJson(keypoints)
    }

    // 将 JSON 字符串转换回 List<KeyPoint>
    @TypeConverter
    fun toKeyPointList(keypointJson: String): List<PointHSVRule> {
        val keyPointType = object : TypeToken<List<PointHSVRule>>() {}.type
        return Gson().fromJson(keypointJson, keyPointType)
    }


}