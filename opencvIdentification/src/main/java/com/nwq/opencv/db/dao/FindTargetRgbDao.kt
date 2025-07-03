package com.nwq.opencv.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

import com.nwq.opencv.db.entity.FindTargetRgbEntity


@Dao
interface FindTargetRgbDao {

    // 根据 keyTag 查询
    @Query("SELECT * FROM find_target_rgb WHERE keyTag = :keyTag")
    fun findByKeyTag(keyTag: String): FindTargetRgbEntity?

    // 删除指定的实体
    @Delete
    fun delete(entity: FindTargetRgbEntity)

    // 插入新的实体
    @Insert
    fun insert(entity: FindTargetRgbEntity)

    @Query("DELETE FROM find_target_rgb WHERE keyTag = :keyTag")
    fun deleteByKeyTag(keyTag: String)
    // 清空表
    @Query("DELETE FROM find_target_rgb")
    fun deleteAll()
}

