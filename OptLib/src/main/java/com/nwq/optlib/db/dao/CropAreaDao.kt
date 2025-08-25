package com.nwq.optlib.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nwq.optlib.db.bean.CropAreaDb
import kotlinx.coroutines.flow.Flow

@Dao
interface CropAreaDao {

    @Query("SELECT * FROM crop_area WHERE keyTag = :keyTag")
    fun findByKeyTag(keyTag: String): CropAreaDb

    @Delete
    fun delete(entity: CropAreaDb)

    @Update
    fun update(entity: CropAreaDb)

    // 根据 keyTag 模糊查询，返回 Flow<List<GrayFilterRuleDb>>
    @Query("SELECT * FROM crop_area WHERE keyTag LIKE '%' || :keyTag || '%'")
    fun findByKeyTagLike(keyTag: String): Flow<List<CropAreaDb>>

    //查询整个表
    @Query("SELECT * FROM crop_area")
    fun findAll(): Flow<List<CropAreaDb>>

    @Insert
    fun insert(entity: CropAreaDb): Long
}