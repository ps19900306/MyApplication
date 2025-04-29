package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


//每一个都是一个可以执行的功能
@Entity(tableName = "function_model")
data class FunctionEntity(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    //识别标签 比如"主菜单","位置菜单"
    var keyTag: String,

    //上次结束时间
    var lastEndTime: Long = 0,

    //是否正常结束
    var isEnd: Boolean = false,

    )