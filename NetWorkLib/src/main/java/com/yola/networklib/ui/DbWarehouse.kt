package com.yola.networklib.ui

import com.nwq.baseutils.ContextUtils
import com.nwq.baseutils.FileUtils
import com.nwq.loguitls.L
import com.nwq.opencv.db.IdentifyDatabase
import com.yola.networklib.remote.DbRemote
import java.io.File
import java.io.FileOutputStream

class DbWarehouse(val dbName: String) {

    private var database: IdentifyDatabase? = null;


    //导出数据库
    suspend fun exportDatabase() {
        val dbPath = FileUtils.exportRoomDatabase(ContextUtils.getContext(), dbName)
        L.d("DbWarehouse", "exportDatabase: $dbPath")
    }


    //判断是否初始化
    suspend fun checkDatabase(): Boolean {
        return IdentifyDatabase.isDatabaseInitialized(dbName);
    }

    suspend fun getDatabaseFromLocal(): Boolean {
        return FileUtils.importRoomDatabase(ContextUtils.getContext(), dbName);
    }

    suspend fun getDatabaseFromAsset(): Boolean {
        return FileUtils.importDatabaseFromAssets(ContextUtils.getContext(), dbName);
    }

    //从远端获取数据库文件
    suspend fun getDataBaseFromRemote(): Boolean {
        return try {
            val dbRemote = DbRemote()
            val response = dbRemote.downloadDatabaseFile(dbName)

            if (response.isSuccess() && response.data != null) {
                val dbPath = ContextUtils.getContext().getDatabasePath(this.dbName)
                val dbFolder = dbPath.parentFile
                if (!dbFolder.exists()) {
                    dbFolder.mkdirs()
                }

                val inputStream = response.data!!.byteStream()
                val outputStream = FileOutputStream(dbPath)
                try {
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (inputStream.read(buffer).also { length = it } > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                    outputStream.flush()
                    true
                } finally {
                    outputStream.close()
                    inputStream.close()
                }
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}