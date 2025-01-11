package com.nwq.opencv.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nwq.opencv.db.entity.TargetVerifyResult
import kotlinx.coroutines.flow.Flow

@Dao
interface TargetVerifyResultDao {

    // 根据 id 查询
    @Query("SELECT * FROM target_verify_result WHERE id = :id")
    fun findById(id: Long): TargetVerifyResult?

    // 根据 tag 查询
    @Query("SELECT * FROM target_verify_result WHERE tag = :tag")
    fun findByTag(tag: String): List<TargetVerifyResult>

    @Query("SELECT * FROM target_verify_result WHERE tag = :tag AND type = :type AND hasFind = :isPass AND isEffective = :isEffective AND isDo = :isDo")
    fun findByTagTypeIsPassIsEffectiveIsDo(
        tag: String,
        type: Int,
        isPass: Boolean,
        isEffective: Boolean,
        isDo: Boolean
    ): List<TargetVerifyResult>

    // 查询所有结果
    @Query("SELECT * FROM target_verify_result")
    fun findAll(): List<TargetVerifyResult>

    // 删除指定的实体
    @Delete
    fun delete(entity: TargetVerifyResult)

    // 插入新的实体
    @Insert
    fun insert(entity: TargetVerifyResult)

    // 更新实体
    @Insert
    fun update(entity: TargetVerifyResult)

    // 清空表
    @Query("DELETE FROM target_verify_result")
    fun deleteAll()

    // 清多余数据  根据他条件 isPass为false isEffective为true isDo为true 条件删除数据
    @Query("DELETE FROM target_verify_result WHERE tag = :tag AND hasFind = false AND isEffective = true AND isDo = true")
    fun deleteByIsPassIsEffectiveIsDo(tag: String )

    // 根据 tag 查询
    @Query("SELECT * FROM target_verify_result WHERE tag = :tag and type = :type")
    fun findByTag(tag: String, type: Int): List<TargetVerifyResult>
}
