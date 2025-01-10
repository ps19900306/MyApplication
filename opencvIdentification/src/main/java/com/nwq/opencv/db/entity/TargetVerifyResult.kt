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
    var id: Long = 0,
    var tag: String="",//这里存的的验证类型
    var hasFind: Boolean = false,//本次结果是否找到目标
    var isEffective: Boolean = false,//本次结果是否有效 通过的结果是否正确
    var isDo:Boolean =false,// 是否进行有效打标签
    var ImgName: String= "",
    var type: Int,
    @TypeConverters(PointVerifyResultConverters::class)
    var poinitInfo: List<PointVerifyResult>? = null,
    var resultArea: CoordinateArea? = null,
    var passCount: Int = 0,
    var failCount: Int = 0,
    var totalCount: Int = 0,
)