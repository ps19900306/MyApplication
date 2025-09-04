package com.nwq.optlib.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nwq.optlib.db.bean.CropAreaDb
import com.nwq.optlib.db.bean.GrayFilterRuleDb
import com.nwq.optlib.db.bean.HsvFilterRuleDb
import kotlinx.coroutines.flow.Flow

@Dao
interface  HsvFilterRuleDao {

    // 根据 keyTag 查询
    @Query("SELECT * FROM hsv_filter_rule WHERE keyTag = :keyTag")
    fun findByKeyTag(keyTag: String): HsvFilterRuleDb?

    @Query("SELECT * FROM hsv_filter_rule WHERE id = :id")
    fun findById(id: Long): HsvFilterRuleDb?

    // 根据 keyTag 查询
    @Query("SELECT * FROM hsv_filter_rule WHERE keyTag = :keyTag")
    fun findByKeyTagFlow(keyTag: String): Flow<HsvFilterRuleDb>?

    // 根据 id 查询
    @Query("SELECT * FROM hsv_filter_rule WHERE id = :id")
    fun findByKeyId(id: Long): HsvFilterRuleDb?

    // 根据 keyTag 模糊查询，返回 Flow<List<HsvFilterRuleDb>>
    @Query("SELECT * FROM hsv_filter_rule WHERE keyTag LIKE '%' || :keyTag || '%'")
    fun findByKeyTagLike(keyTag: String): Flow<List<HsvFilterRuleDb>>

    //查询整个表
    @Query("SELECT * FROM hsv_filter_rule")
    fun findAll(): Flow<List<HsvFilterRuleDb>>

    // 删除指定的实体
    @Delete
    fun delete(entity: HsvFilterRuleDb)

    // 删除多个实体
    @Delete
    fun delete(entities: Array<HsvFilterRuleDb>)

    // 更新指定的实体
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(entity: HsvFilterRuleDb)

    // 插入新的实体
    @Insert
    fun insert(entity: HsvFilterRuleDb): Long

    // 清空表
    @Query("DELETE FROM hsv_filter_rule")
    fun deleteAll()



}