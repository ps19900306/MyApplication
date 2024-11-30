package com.nwq.opencv.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nwq.opencv.db.entity.FindTargetMatEntity




@Dao
interface FindTargetMatDao {

    // 根据 keyTag 查询
    @Query("SELECT * FROM find_target_mat WHERE keyTag = :keyTag")
    fun findByKeyTag(keyTag: String): FindTargetMatEntity?

    // 删除指定的实体
    @Delete
    fun delete(entity: FindTargetMatEntity)

    // 插入新的实体
    @Insert
    fun insert(entity: FindTargetMatEntity)

    // 清空表
    @Query("DELETE FROM find_target_mat")
    fun deleteAll()
}

