package com.nwq.opencv.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nwq.opencv.db.entity.FindTargetHsvEntity
import com.nwq.opencv.db.entity.FindTargetRecord
import kotlinx.coroutines.flow.Flow


@Dao
interface FindTargetRecordDao {

    // 根据 id 查询
    @Query("SELECT * FROM find_target_all WHERE id = :id")
    fun findById(id: Long): FindTargetRecord?

    // 根据 keyTag 查询
    @Query("SELECT * FROM find_target_all WHERE keyTag = :keyTag")
    fun findByKeyTag(keyTag: String): FindTargetRecord?

    // 根据 keyTag 模糊查询，返回 Flow<List<FindTargetRecord>>
    @Query("SELECT * FROM find_target_all WHERE keyTag LIKE '%' || :keyTag || '%'")
    fun findByKeyTagLike(keyTag: String): Flow<List<FindTargetRecord>>

    //查询整个表
    @Query("SELECT * FROM find_target_all")
    fun findAll(): Flow<List<FindTargetRecord>>

    // 删除指定的实体
    @Delete
    fun delete(entity: FindTargetRecord)

    // 插入新的实体
    @Insert
    fun insert(entity: FindTargetRecord)

    // 清空表
    @Query("DELETE FROM find_target_all")
    fun deleteAll()
}
