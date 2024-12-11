package com.nwq.opencv.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nwq.opencv.hsv.HSVRule
import com.nwq.opencv.hsv.PointHSVRule
import org.opencv.core.Point


class HSVRuleConverters {

    // 将 List<KeyPoint> 转换为 JSON 字符串
    @TypeConverter
    fun fromKeyPointList(keypoints: List<HSVRule>): String {
        return Gson().toJson(keypoints)
    }

    // 将 JSON 字符串转换回 List<KeyPoint>
    @TypeConverter
    fun toKeyPointList(keypointJson: String): List<HSVRule> {
        val keyPointType = object : TypeToken<List<HSVRule>>() {}.type
        return Gson().fromJson(keypointJson, keyPointType)
    }


}