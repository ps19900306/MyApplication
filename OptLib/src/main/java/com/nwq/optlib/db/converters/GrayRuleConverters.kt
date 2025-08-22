package com.nwq.optlib.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nwq.baseutils.GsonUtils
import com.nwq.optlib.bean.GrayRule


class GrayRuleConverters {

    // 将 List<KeyPoint> 转换为 JSON 字符串
    @TypeConverter
    fun fromHSVRuleList(list: List<GrayRule>): String {
        return GsonUtils.toJson(list)
    }

    // 将 JSON 字符串转换回 List<KeyPoint>
    @TypeConverter
    fun toHSVRuleList(str: String): List<GrayRule> {
        val keyPointType = object : TypeToken<List<GrayRule>>() {}.type
        return Gson().fromJson(str, keyPointType)
    }


}