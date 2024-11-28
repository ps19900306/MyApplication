package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "click_area")
data class ClickEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val keyTag: String,
    val x: Int,
    val y: Int,
    val with: Int,
    val height: Int,
    val isRound: Boolean,
    val offsetX: Int,
    val offsetY: Int,
)
