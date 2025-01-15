package com.nwq.opencv.db

import com.nwq.opencv.db.entity.ImageDescriptorEntity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nwq.baseutils.ContextUtils
import com.nwq.opencv.db.converters.CoordinateAreaConverters
import com.nwq.opencv.db.converters.HSVRuleConverters
import com.nwq.opencv.db.converters.KeyPointConverters
import com.nwq.opencv.db.converters.LongListConverters
import com.nwq.opencv.db.converters.PointConverters
import com.nwq.opencv.db.converters.PointHSVRuleConverters
import com.nwq.opencv.db.converters.PointRuleConverters
import com.nwq.opencv.db.converters.PointVerifyResultConverters
import com.nwq.opencv.db.dao.AutoRulePointDao
import com.nwq.opencv.db.dao.FindTargetHsvDao
import com.nwq.opencv.db.dao.FindTargetImgDao
import com.nwq.opencv.db.dao.FindTargetMatDao
import com.nwq.opencv.db.dao.FindTargetRecordDao
import com.nwq.opencv.db.dao.FindTargetRgbDao
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
import com.nwq.opencv.db.entity.KeyPointEntity
import com.nwq.opencv.db.entity.LogicEntity
import com.nwq.opencv.db.entity.TargetVerifyResult
import java.io.FileOutputStream
import java.io.InputStream

@Database(entities = [AutoRulePointEntity::class, ClickEntity::class, FindTargetHsvEntity::class
    , FindTargetImgEntity::class , FindTargetMatEntity::class , FindTargetRecord::class ,
    FindTargetRgbEntity::class , ImageDescriptorEntity::class , KeyPointEntity::class , LogicEntity::class,TargetVerifyResult::class],
    version = 1,exportSchema = false)
@TypeConverters(CoordinateAreaConverters::class,HSVRuleConverters::class, KeyPointConverters::class, LongListConverters::class,
    PointConverters::class,
    PointHSVRuleConverters::class,
    PointRuleConverters::class,
    PointVerifyResultConverters::class)
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

         //        检查数据库是否已初始化
                if (!isDatabaseInitialized(context)) {
                    // 从assets目录中导入数据库文件
                    importDatabaseFromAssets(context)
                }

                INSTANCE = instance
                instance
            }
        }

        private fun isDatabaseInitialized(context: Context): Boolean {
            val dbPath = context.getDatabasePath("identify_database")
            return dbPath.exists() && dbPath.length() > 0
        }

        private fun importDatabaseFromAssets(context: Context) {
            val dbPath = context.getDatabasePath("identify_database")
            val dbFolder = dbPath.parentFile
            if (!dbFolder.exists()) {
                dbFolder.mkdirs()
            }

            val assetManager = context.assets
            val databaseAssetName = "identify_database.db"

            try {
                val inputStream: InputStream = assetManager.open(databaseAssetName)
                val outputStream = FileOutputStream(dbPath)
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.flush()
                outputStream.close()
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
                // 如果从assets中导入失败，则创建一个新的空数据库
                createEmptyDatabase(context)
            }
        }

        private fun createEmptyDatabase(context: Context) {
            Room.databaseBuilder(
                context.applicationContext,
                IdentifyDatabase::class.java,
                "identify_database"
            ).build()
        }
    }
}
