package com.nwq.opencv.db

import ImageDescriptorEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface ImageDescriptorDao {

    @Insert
    suspend fun insertDescriptor(entity: ImageDescriptorEntity)

    @Query("SELECT * FROM image_descriptors WHERE keyTag = :keyTag")
    suspend fun getDescriptor(keyTag: String): ImageDescriptorEntity?

}