package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "click_area")
data class ClickEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var keyTag: String,
    var x: Int,
    var y: Int,
    var with: Int,
    var height: Int,
    var isRound: Boolean,
    var offsetX: Int,
    var offsetY: Int,
)
