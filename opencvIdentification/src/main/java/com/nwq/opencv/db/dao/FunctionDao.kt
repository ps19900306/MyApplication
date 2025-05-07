package com.nwq.opencv.db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nwq.opencv.db.entity.FunctionEntity
import kotlinx.coroutines.flow.Flow

interface FunctionDao {
    @Query("SELECT * FROM function_model WHERE keyTag = :keyTag")
    fun findByKeyTag(keyTag: String): FunctionEntity

    @Query("SELECT * FROM function_model WHERE keyTag = :keyTag")
    fun findByKeyTagFlow(keyTag: String): Flow<FunctionEntity>

    @Query("SELECT * FROM function_model WHERE id = :id")
    fun findByKeyId(id: Long): FunctionEntity

    @Query("SELECT * FROM function_model WHERE keyTag LIKE '%' || :keyTag || '%'")
    fun findByKeyTagLike(keyTag: String): Flow<List<FunctionEntity>>

    @Query("SELECT * FROM function_model")
    fun findAll(): Flow<List<FunctionEntity>>

    @Delete
    fun delete(entity: FunctionEntity)

    @Insert
    fun insert(entity: FunctionEntity)
}