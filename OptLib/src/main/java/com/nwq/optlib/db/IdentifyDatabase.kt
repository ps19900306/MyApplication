package com.nwq.optlib.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nwq.baseutils.ContextUtils
import com.nwq.optlib.db.bean.CropAreaDb
import com.nwq.optlib.db.bean.GrayFilterRuleDb
import com.nwq.optlib.db.bean.HsvFilterRuleDb
import com.nwq.optlib.db.converters.CoordinateAreaConverters
import com.nwq.optlib.db.converters.GrayRuleConverters
import com.nwq.optlib.db.converters.HSVRuleConverters
import com.nwq.optlib.db.converters.PointHSVRuleConverters
import com.nwq.optlib.db.dao.CropAreaDao
import com.nwq.optlib.db.dao.GrayFilterRuleDao
import com.nwq.optlib.db.dao.HsvFilterRuleDao


@Database(
    entities = [CropAreaDb::class, GrayFilterRuleDb::class, HsvFilterRuleDb::class],
    version = 1, exportSchema = false
)
@TypeConverters(
    CoordinateAreaConverters::class,
    HSVRuleConverters::class,
    GrayRuleConverters::class,
    PointHSVRuleConverters::class
)
abstract class IdentifyDatabase : RoomDatabase() {

    abstract fun cropAreaDao(): CropAreaDao

    abstract fun grayFilterRuleDao(): GrayFilterRuleDao

    abstract fun hsvFilterRuleDao(): HsvFilterRuleDao


    companion object {
        @Volatile
        private var INSTANCE: IdentifyDatabase? = null

        private var lastDbName = "identify_database"

        public fun getDatabase(
        ): IdentifyDatabase {
            return getDatabase(ContextUtils.getContext(), lastDbName)
        }

        public fun getDatabase(
            name: String
        ): IdentifyDatabase {
            return getDatabase(ContextUtils.getContext(), name)
        }


        //以后每一个游戏操作单独为一个数据库
        public fun getDatabase(
            context: Context,
            name: String
        ): IdentifyDatabase {
            if (INSTANCE != null && lastDbName == name)
                return INSTANCE!!

            //更新数据库名称
            lastDbName = name

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IdentifyDatabase::class.java,
                    name
                ).build()
                INSTANCE = instance
                instance
            }
        }

        public fun isDatabaseInitialized(
            name: String = "identify_database"
        ): Boolean {
            val dbPath = ContextUtils.getContext().getDatabasePath(name)
            return dbPath.exists() && dbPath.length() > 0
        }

    }
}