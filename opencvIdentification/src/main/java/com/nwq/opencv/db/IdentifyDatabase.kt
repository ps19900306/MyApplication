package com.nwq.opencv.db

import ImageDescriptorEntity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nwq.baseutils.ContextUtils



@Database(entities = [ImageDescriptorEntity::class], version = 1)
abstract class IdentifyDatabase: RoomDatabase() {

    abstract fun imageDescriptorDao(): ImageDescriptorDao

    companion object {
        @Volatile
        private var INSTANCE: IdentifyDatabase? = null
        fun getDatabase(context: Context = ContextUtils.getContext()): IdentifyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IdentifyDatabase::class.java,
                    "identify_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}