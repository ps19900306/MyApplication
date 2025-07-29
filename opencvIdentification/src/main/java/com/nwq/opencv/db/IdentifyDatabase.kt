package com.nwq.opencv.db

import com.nwq.opencv.db.entity.ImageDescriptorEntity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nwq.baseutils.ContextUtils
import com.nwq.opencv.IFindTarget
import com.nwq.opencv.db.converters.CoordinateAreaConverters
import com.nwq.opencv.db.converters.HSVRuleConverters
import com.nwq.opencv.db.converters.KeyPointConverters
import com.nwq.opencv.db.converters.LongListConverters
import com.nwq.opencv.db.converters.PointConverters
import com.nwq.opencv.db.converters.PointHSVRuleConverters
import com.nwq.opencv.db.converters.PointRuleConverters
import com.nwq.opencv.db.converters.PointVerifyResultConverters
import com.nwq.opencv.db.dao.AutoRulePointDao
import com.nwq.opencv.db.dao.ClickDao
import com.nwq.opencv.db.dao.FindTargetHsvDao
import com.nwq.opencv.db.dao.FindTargetImgDao
import com.nwq.opencv.db.dao.FindTargetMatDao
import com.nwq.opencv.db.dao.FindTargetRecordDao
import com.nwq.opencv.db.dao.FindTargetRgbDao
import com.nwq.opencv.db.dao.FunctionDao
import com.nwq.opencv.db.dao.ImageDescriptorDao
import com.nwq.opencv.db.dao.LogicDao
import com.nwq.opencv.db.dao.TargetVerifyResultDao
import com.nwq.opencv.db.entity.AutoRulePointEntity
import com.nwq.opencv.db.entity.ClickEntity
import com.nwq.opencv.db.entity.FindTargetHsvEntity
import com.nwq.opencv.db.entity.FindTargetImgEntity
import com.nwq.opencv.db.entity.FindTargetMatEntity
import com.nwq.opencv.db.entity.FindTargetRecord
import com.nwq.opencv.db.entity.FindTargetRgbEntity
import com.nwq.opencv.db.entity.FunctionEntity
import com.nwq.opencv.db.entity.KeyPointEntity
import com.nwq.opencv.db.entity.LogicEntity
import com.nwq.opencv.db.entity.TargetVerifyResult
import java.io.FileOutputStream
import java.io.InputStream

@Database(
    entities = [AutoRulePointEntity::class, ClickEntity::class, FindTargetHsvEntity::class, FindTargetImgEntity::class, FindTargetMatEntity::class, FindTargetRecord::class,
        FindTargetRgbEntity::class, FunctionEntity::class, ImageDescriptorEntity::class, KeyPointEntity::class, LogicEntity::class, TargetVerifyResult::class],
    version = 1, exportSchema = false
)
@TypeConverters(
    CoordinateAreaConverters::class,
    HSVRuleConverters::class,
    KeyPointConverters::class,
    LongListConverters::class,
    PointConverters::class,
    PointHSVRuleConverters::class,
    PointRuleConverters::class,
    PointVerifyResultConverters::class
)
abstract class IdentifyDatabase : RoomDatabase() {

    abstract fun imageDescriptorDao(): ImageDescriptorDao

    abstract fun findTargetHsvDao(): FindTargetHsvDao

    abstract fun findTargetRgbDao(): FindTargetRgbDao

    abstract fun findTargetImgDao(): FindTargetImgDao

    abstract fun findTargetMatDao(): FindTargetMatDao

    abstract fun findTargetRecordDao(): FindTargetRecordDao

    abstract fun autoRulePointDao(): AutoRulePointDao

    abstract fun targetVerifyResultDao(): TargetVerifyResultDao

    abstract fun logicDao(): LogicDao

    abstract fun functionDao(): FunctionDao

    abstract fun clickDao(): ClickDao

    companion object {
        @Volatile
        private var INSTANCE: IdentifyDatabase? = null

        private var lastDbName = "identify_database"

        public fun getDatabase(
        ): IdentifyDatabase {
            return getDatabase(ContextUtils.getContext(), lastDbName)
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
            context: Context,
            name: String = "identify_database"
        ): Boolean {
            val dbPath = context.getDatabasePath(name)
            return dbPath.exists() && dbPath.length() > 0
        }

    }
}