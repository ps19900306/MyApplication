package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

//每一个都是一个可以执行的功能
@Entity(tableName = "function_ex_end")
data class FunctionExEndEntity(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    //
    var FunctionId: Long,

    //开始时间
    var startTime: Long = 0,

    //结束时间
    var endTime: Long = 0,

    //异常结束的类型
    var Type: Int = 0,

    )