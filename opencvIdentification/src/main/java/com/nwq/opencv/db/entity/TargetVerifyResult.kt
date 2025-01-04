package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.baseobj.CoordinateArea
import com.nwq.opencv.data.PointVerifyResult
import com.nwq.opencv.db.converters.PointVerifyResultConverters

@Entity(tableName = "target_verify_result")
data class TargetVerifyResult(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var tag: String="",//这里存的的验证类型
    var isPass: Boolean = false,
    var ImgName: String= "",
    var type: Int,
    @TypeConverters(PointVerifyResultConverters::class)
    var poinitInfo: List<PointVerifyResult>? = null,
    var resultArea: CoordinateArea? = null,
    var passCount: Int = 0,
    var failCount: Int = 0,
    var totalCount: Int = 0,
)