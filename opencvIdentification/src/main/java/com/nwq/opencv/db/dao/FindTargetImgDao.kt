package com.nwq.opencv.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

import com.nwq.opencv.db.entity.FindTargetImgEntity


@Dao
interface FindTargetImgDao {

    // 根据 keyTag 查询
    @Query("SELECT * FROM find_target_img WHERE keyTag = :keyTag")
    fun findByKeyTag(keyTag: String): FindTargetImgEntity?

    // 删除指定的实体
    @Delete
    fun delete(entity: FindTargetImgEntity)

    // 插入新的实体
    @Insert
    fun insert(entity: FindTargetImgEntity)

    // 清空表
    @Query("DELETE FROM find_target_img")
    fun deleteAll()
}

