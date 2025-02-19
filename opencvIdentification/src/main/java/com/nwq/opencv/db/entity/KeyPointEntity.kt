package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// 数据库中的 KeyPoint 数据结构
@Entity(tableName = "keypoints_table")
data class KeyPointEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    // 将 `KeyPoint` 序列化为 JSON 字符串存储
    var keypointJson: String
)