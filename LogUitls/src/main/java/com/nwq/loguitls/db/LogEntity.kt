package com.nwq.loguitls.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logs")
data class LogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tag: String,
    val msg: String,
    val level: Int,
    val createTime: Long,
    val recordTime: Long = System.currentTimeMillis()
)