package com.nwq.opencv.db.entity

import androidx.room.Entity
import com.nwq.baseobj.CoordinateArea

@Entity(tableName = "find_target")
data class FindTargetEntity(
    val tag: String,
    val targetOriginalArea: CoordinateArea,
    var findArea: CoordinateArea?=null,
    val type:Int=0,

    //点识别使用
    var errorTolerance: Int = 0,
    var pointValueInfo:String,


)

