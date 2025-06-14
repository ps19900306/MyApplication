package com.nwq.opencv.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nwq.opencv.db.entity.AutoRulePointEntity
import com.nwq.opencv.db.entity.FindTargetRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AutoRulePointDao {


    // 根据 keyTag 查询
    @Query("SELECT * FROM auto_rule_point WHERE keyTag = :keyTag")
    fun findByKeyTag(keyTag: String): AutoRulePointEntity?

    // 根据 keyTag 查询
    @Query("SELECT * FROM auto_rule_point WHERE keyTag = :keyTag")
    fun findByKeyTagFlow(keyTag: String): Flow<AutoRulePointEntity>?

    // 根据 keyTag 查询
    @Query("SELECT * FROM auto_rule_point WHERE id = :id")
    fun findByKeyId(id: Long): AutoRulePointEntity?


    // 根据 keyTag 模糊查询，返回 Flow<List<AutoRulePointEntity>>
    @Query("SELECT * FROM auto_rule_point WHERE keyTag LIKE '%' || :keyTag || '%'")
    fun findByKeyTagLike(keyTag: String): Flow<List<AutoRulePointEntity>>

    //查询整个表
    @Query("SELECT * FROM auto_rule_point")
    fun findAll(): Flow<List<AutoRulePointEntity>>

    // 删除指定的实体
    @Delete
    fun delete(entity: AutoRulePointEntity)

    // 删除指定的实体
    @Delete
    fun delete(entitys: Array<AutoRulePointEntity>)

    // 更新指定的实体
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(entity: AutoRulePointEntity)

    // 插入新的实体
    @Insert
    fun insert(entity: AutoRulePointEntity):Long

    // 清空表
    @Query("DELETE FROM auto_rule_point")
    fun deleteAll()
}