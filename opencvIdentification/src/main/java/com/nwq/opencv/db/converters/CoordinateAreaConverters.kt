package com.nwq.opencv.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nwq.baseobj.CoordinateArea
import org.opencv.core.KeyPoint

class CoordinateAreaConverters {


    @TypeConverter
    fun fromKeyPointList(area: CoordinateArea): String {
        return Gson().toJson(area)
    }

    // 将 JSON 字符串转换回 List<KeyPoint>
    @TypeConverter
    fun toKeyPointList(areaJson: String): CoordinateArea {
        val keyPointType = object : TypeToken<CoordinateArea>() {}.type
        return Gson().fromJson(areaJson, keyPointType)
    }
}