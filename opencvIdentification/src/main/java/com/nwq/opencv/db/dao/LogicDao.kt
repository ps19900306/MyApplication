package com.nwq.opencv.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nwq.opencv.db.entity.FunctionEntity
import com.nwq.opencv.db.entity.LogicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogicDao {

    // 根据 keyTag 查询
    @Query("SELECT * FROM logic_unit WHERE keyTag = :keyTag")
    fun findByKeyTag(keyTag: String): LogicEntity?

    // 根据 keyTag 查询
    @Query("SELECT * FROM logic_unit WHERE id = :id")
    fun findByKeyId(id: Long): LogicEntity?

    @Query("SELECT * FROM logic_unit WHERE functionId = :functionId and parentLogicId = 0")
    fun findByFunctionIdRoot(functionId: Long): MutableList<LogicEntity>

    @Query("SELECT * FROM logic_unit WHERE functionId = :id")
    fun findByFunctionId(id: Long): List<LogicEntity>

    @Query("SELECT * FROM logic_unit WHERE functionId = :id")
    fun findByFunctionIdFlow(id: Long): Flow<List<LogicEntity>>

    @Query("SELECT * FROM logic_unit")
    fun findAll(): Flow<List<LogicEntity>>

    // 删除指定的实体
    @Delete
    fun delete(entity: LogicEntity)

    // 插入新的实体
    @Insert
    fun insert(entity: LogicEntity): Long

    // 清空表
    @Query("DELETE FROM logic_unit")
    fun deleteAll()

    @Query("SELECT * FROM logic_unit WHERE keyTag LIKE '%' || :keyTag || '%'")
    fun findByKeyTagLike(keyTag: String): Flow<List<LogicEntity>>

    @Query("SELECT * FROM logic_unit WHERE keyTag LIKE '%' || :keyTag || '%' and functionId = :functionId")
    fun findByKeyTagFunctionIdLike(keyTag: String, functionId: Long): Flow<List<LogicEntity>>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    fun update(entity: LogicEntity)
}