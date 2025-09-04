package com.nwq.optlib.db.dao

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nwq.optlib.db.bean.CropAreaDb
import com.nwq.optlib.db.bean.GrayFilterRuleDb
import kotlinx.coroutines.flow.Flow

/**
 * [com.nwq.optlib.db.bean.GrayFilterRuleDb]
 */
@Dao
interface GrayFilterRuleDao {

    // 根据 keyTag 查询
    @Query("SELECT * FROM gray_filter_rule WHERE keyTag = :keyTag")
    fun findByKeyTag(keyTag: String): GrayFilterRuleDb?

    @Query("SELECT * FROM gray_filter_rule WHERE id = :id")
    fun findById(id: Long): GrayFilterRuleDb?
    // 根据 keyTag 查询
    @Query("SELECT * FROM gray_filter_rule WHERE keyTag = :keyTag")
    fun findByKeyTagFlow(keyTag: String): Flow<GrayFilterRuleDb>?

    // 根据 keyTag 查询
    @Query("SELECT * FROM gray_filter_rule WHERE id = :id")
    fun findByKeyId(id: Long): GrayFilterRuleDb?


    // 根据 keyTag 模糊查询，返回 Flow<List<GrayFilterRuleDb>>
    @Query("SELECT * FROM gray_filter_rule WHERE keyTag LIKE '%' || :keyTag || '%'")
    fun findByKeyTagLike(keyTag: String): Flow<List<GrayFilterRuleDb>>

    //查询整个表
    @Query("SELECT * FROM gray_filter_rule")
    fun findAll(): Flow<List<GrayFilterRuleDb>>

    // 删除指定的实体
    @Delete
    fun delete(entity: GrayFilterRuleDb)

    // 删除指定的实体
    @Delete
    fun delete(entitys: Array<GrayFilterRuleDb>)

    // 更新指定的实体
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(entity: GrayFilterRuleDb)

    // 插入新的实体
    @Insert
    fun insert(entity: GrayFilterRuleDb): Long

    // 清空表
    @Query("DELETE FROM gray_filter_rule")
    fun deleteAll()

}