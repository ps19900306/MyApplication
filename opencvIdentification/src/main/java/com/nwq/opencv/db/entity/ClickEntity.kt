package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "click_area")
data class ClickEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var keyTag: String,
    var x: Int,  //创建点击区域的原始坐标x
    var y: Int,//创建点击区域的原始坐标y
    var with: Int,
    var height: Int,
    var isRound: Boolean,
    var offsetX: Int, //创建点击区域的原始 相对找寻目标坐标的偏移量X
    var offsetY: Int, //创建点击区域的原始 相对找寻目标坐标的偏移量Y
)
