package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.opencv.data.PointVerifyResult
import com.nwq.opencv.db.converters.PointVerifyResultConverters

@Entity(tableName = "target_verify_result")
data class TargetVerifyResult(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var tag: String,
    @TypeConverters(PointVerifyResultConverters::class)
    var poinitInfo: List<PointVerifyResult>?,
    var isPass: Boolean,
    var ImgName: String,
    var Type: Int
)