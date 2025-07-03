package com.nwq.opencv.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nwq.opencv.db.entity.FindTargetHsvEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface FindTargetHsvDao {

    // 根据 keyTag 查询
    @Query("SELECT * FROM find_target_hsv WHERE keyTag = :keyTag")
    fun findByKeyTag(keyTag: String): FindTargetHsvEntity?


    // 删除指定的实体
    @Delete
    fun delete(entity: FindTargetHsvEntity)

    @Query("DELETE FROM find_target_hsv WHERE keyTag = :keyTag")
    fun deleteByKeyTag(keyTag: String)

    @Update
    fun update(entity: FindTargetHsvEntity)


    // 插入新的实体
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: FindTargetHsvEntity)

    // 清空表
    @Query("DELETE FROM find_target_hsv")
    fun deleteAll()



}