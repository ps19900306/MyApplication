package com.nwq.loguitls.file

import android.os.Environment
import android.util.Log
import com.nwq.baseutils.ContextUtils
import com.nwq.baseutils.DataUtils
import com.nwq.baseutils.FileUtils
import com.nwq.loguitls.ILog
import com.nwq.loguitls.LogFilterInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.coroutines.CoroutineContext

class FileLog(val log: LogFilterInfo? = null) : ILog, CoroutineScope {

    private val MAX_LOG_COUNT = 300 // 缓存的最大日志数量
    private val MAX_LOG_AGE_DAYS = 7 // 日志文件的最大保留天数
    private val logQueue = mutableListOf<Pair<Long, String>>()
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private var lastFileTime = DataUtils.getEndOfDayTimestamp()
    private var fileName = DataUtils.getCurrentDateTime() + ".txt"


    init {
        cleanOldLogs()
    }
    override fun v(tag: String, msg: String, time: Long) {
        writeLogToFile(System.currentTimeMillis(), ":$tag :v: $msg")
    }

    override fun d(tag: String, msg: String, time: Long) {
        writeLogToFile(System.currentTimeMillis(), ":$tag :d: $msg")
    }

    override fun i(tag: String, msg: String, time: Long) {
        writeLogToFile(System.currentTimeMillis(), ":$tag :i: $msg")
    }

    override fun w(tag: String, msg: String, time: Long) {
        writeLogToFile(System.currentTimeMillis(), ":$tag :w: $msg")
    }

    override fun e(tag: String, msg: String, time: Long) {
        writeLogToFile(System.currentTimeMillis(), ":$tag :e: $msg")
    }

    override fun cancel() {
        job.cancel()
    }

    override fun getLogFilterInfo(): LogFilterInfo? {
        return log;
    }

    private fun updateFileName() {
        lastFileTime = DataUtils.getEndOfDayTimestamp()
        fileName = DataUtils.getCurrentDateTime() + ".txt"
    }

    private fun writeLogToFile(time: Long, info: String) {
        synchronized(logQueue) {
            if (time > lastFileTime) {
                launch {
                    flushLog()
                    delay(3000)
                    updateFileName()
                    logQueue.add(Pair(time, info))
                }
            } else {
                logQueue.add(Pair(time, info))
                if (logQueue.size >= MAX_LOG_COUNT) {
                    flushLogs()
                } else {
                }
            }
        }
    }

    private suspend fun flushLog() {
        val file = FileUtils.checkDocumentsFile(fileName) ?: return
        synchronized(logQueue) {
            if (logQueue.isEmpty()) return
            val logsToWrite = logQueue.toList()
            logQueue.clear()

            try {
                val writer = FileWriter(file, true)
                for ((time, logEntry) in logsToWrite.sortedBy { it.first }) {
                    writer.appendLine(DataUtils.millisToDateTimeStr(time) + logEntry)
                }
                writer.close()
            } catch (e: IOException) {
                Log.e("LogWriter", "Error writing to log file", e)
            }
        }
    }

    override fun flushLogs() {
        launch {
            flushLog()
        }
    }

    //android
    //如果我在 context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) 存放日志文件
    //日志文件格式是yyyy-MM-dd.text。我希望删除掉七天之前的文件
    private fun cleanOldLogs() {
        val context = ContextUtils.getContext()
        val logDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        logDir?.let { dir ->
            if (dir.exists() && dir.isDirectory) {
                // 获取当前日期，设置为7天前
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -MAX_LOG_AGE_DAYS)
                val sevenDaysAgo = calendar.time

                // 创建日期格式器，匹配文件名格式 "yyyy-MM-dd"
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                // 遍历目录中的文件
                dir.listFiles()?.forEach { file ->
                    // 检查文件是否符合日志文件命名格式
                    try {
                        val fileDate = dateFormat.parse(file.nameWithoutExtension)
                        if (fileDate != null && fileDate.before(sevenDaysAgo)) {
                            // 删除7天前的文件
                            if (file.delete()) {
                                println("${file.name} deleted")
                            } else {
                                println("Failed to delete ${file.name}")
                            }
                        }
                    } catch (e: Exception) {
                        // 解析文件名日期失败，忽略
                        Log.e("FileLog", "Failed to parse date from filename ${file.name}", e)
                    }
                }
            }
        }
    }





}