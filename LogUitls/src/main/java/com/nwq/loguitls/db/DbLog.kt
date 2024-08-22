package com.nwq.loguitls.db


import com.nwq.loguitls.ILog
import com.nwq.loguitls.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.coroutines.CoroutineContext

class DbLog : ILog, CoroutineScope {

    private val logQueue = mutableListOf<LogEntity>()
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private val MAX_LOG_COUNT = 100 // 缓存的最大日志数量
    private val MAX_LOG_AGE_DAYS = 7 // 日志文件的最大保留天数

    init {
        cleanOldLogs()
    }

    override fun v(tag: String, msg: String, time: Long) {
        writeLogToFile(tag, msg, time, LogLevel.VERBOSE)
    }

    override fun d(tag: String, msg: String, time: Long) {
        writeLogToFile(tag, msg, time, LogLevel.DEBUG)
    }

    override fun i(tag: String, msg: String, time: Long) {
        writeLogToFile(tag, msg, time, LogLevel.INFO)
    }

    override fun w(tag: String, msg: String, time: Long) {
        writeLogToFile(tag, msg, time, LogLevel.WARN)
    }

    override fun e(tag: String, msg: String, time: Long) {
        writeLogToFile(tag, msg, time, LogLevel.ERROR)
    }

    override fun cancel() {
        job.cancel()
    }

    override fun flushLogs() {
        val logsToWrite: List<LogEntity>
        synchronized(logQueue) {
            logsToWrite = logQueue.toList()
            logQueue.clear()
        }
        launch {
            val dao = LogDatabase.getDatabase().logDao()
            dao.insert(logsToWrite)
        }
    }

    private fun writeLogToFile(tag: String, msg: String, time: Long, level: Int) {
        synchronized(logQueue) {
            val nowTime = System.currentTimeMillis()
            logQueue.add(
                LogEntity(
                    tag = tag,
                    msg = msg,
                    createTime = time,
                    level = level,
                    recordTime = nowTime
                )
            )
            if (logQueue.size >= MAX_LOG_COUNT) {
                flushLogs()
            }
        }
    }

    //删除七天前的日志
    private fun cleanOldLogs() {
        launch {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -MAX_LOG_AGE_DAYS)
            val sevenDaysAgo = calendar.timeInMillis
            LogDatabase.getDatabase().logDao().deleteLogsBefore(sevenDaysAgo)
        }
    }
}