package com.nwq.opencv.db.converters

import android.text.TextUtils
import androidx.room.TypeConverter
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.GsonUtils

class CoordinateAreaConverters {



    @TypeConverter
    fun fromCoordinateArea(area: CoordinateArea?): String {
        return if (area == null){
            ""
        }else{
           GsonUtils.toJson(area)
        }
    }

    // 将 JSON 字符串转换回 List<KeyPoint>
    @TypeConverter
    fun toCoordinateArea(areaJson: String): CoordinateArea? {
        if (TextUtils.isEmpty(areaJson)){
            return null
        }
        return GsonUtils.fromJson(areaJson, CoordinateArea::class.java)
    }
}