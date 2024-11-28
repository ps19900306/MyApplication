package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.nwq.baseobj.CoordinateArea
import com.nwq.opencv.db.converters.CoordinateAreaConverters
import com.nwq.opencv.find_target.FindTarget

@Entity(tableName = "find_target")
data class FindTargetEntity(
    val tag: String,


    @TypeConverters(CoordinateAreaConverters::class)
    val targetOriginalArea: CoordinateArea,

    @TypeConverters(CoordinateAreaConverters::class)
    var findArea: CoordinateArea?=null,
    val type:Int=0,

    //点识别使用
    var errorTolerance: Int = 0,
    var pointValueInfo:String,

){

}

