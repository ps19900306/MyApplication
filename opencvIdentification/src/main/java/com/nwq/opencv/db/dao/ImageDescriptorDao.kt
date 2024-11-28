package com.nwq.opencv.db.dao

import com.nwq.opencv.db.entity.ImageDescriptorEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface ImageDescriptorDao {

    @Insert
    fun insertDescriptor(entity: ImageDescriptorEntity)

    @Query("SELECT * FROM image_descriptors WHERE keyTag = :keyTag")
    fun getDescriptor(keyTag: String): ImageDescriptorEntity?



}