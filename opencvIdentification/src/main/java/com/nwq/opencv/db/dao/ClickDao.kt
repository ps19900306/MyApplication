package com.nwq.opencv.db.dao

import androidx.room.*
import com.nwq.opencv.db.entity.ClickEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClickDao {

    // 根据 keyTag 查询单个 ClickEntity
    @Query("SELECT * FROM click_area WHERE keyTag = :keyTag")
    fun findByKeyTag(keyTag: String): ClickEntity?

    // 返回 Flow 类型的单个实体
    @Query("SELECT * FROM click_area WHERE keyTag = :keyTag")
    fun findByKeyTagFlow(keyTag: String): Flow<ClickEntity?>

    // 根据 id 查询单个实体
    @Query("SELECT * FROM click_area WHERE id = :id")
    fun findByKeyId(id: Long): ClickEntity?

    // 返回 Flow 类型的根据 id 查询结果
    @Query("SELECT * FROM click_area WHERE id = :id")
    fun findByKeyIdFlow(id: Long): Flow<ClickEntity?>

    // 模糊查询 keyTag
    @Query("SELECT * FROM click_area WHERE keyTag LIKE '%' || :keyTag || '%'")
    fun findByKeyTagLike(keyTag: String): Flow<List<ClickEntity>>

    // 查询所有数据
    @Query("SELECT * FROM click_area")
    fun findAll(): Flow<List<ClickEntity>>

    // 插入一条数据，返回主键 id
    @Insert
    fun insert(entity: ClickEntity): Long

    // 删除一条数据
    @Delete
    fun delete(entity: ClickEntity)

    // 批量删除
    @Delete
    fun delete(entities: Array<out ClickEntity>)

    // 删除所有数据
    @Query("DELETE FROM click_area")
    fun deleteAll()


}
