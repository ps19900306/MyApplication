package com.nwq.loguitls

import com.nwq.baseutils.DataUtils
import com.nwq.loguitls.cat.CatLog
import com.nwq.loguitls.db.DbLog
import com.nwq.loguitls.file.FileLog

//日志输出的类
object L : ILog {

    private val list = mutableListOf<ILog>()


    //初始化打印哪些日志
    init {
        // list.add(FileLog(LogFilterInfo(level = LogLevel.INFO))) //日志文件  默认不开启
        list.add(CatLog()) //普通日志 只在调试模式的时候显示
        list.add(DbLog(LogFilterInfo(level = LogLevel.INFO))) //数据库日志
    }

    /**
     * 注意这里 方法里面的time: Long 是用来做过滤 让日志不要太多
     * it.v 里面的Time是触发此事件时候的 时间  用来记录事件的
     * 二个 time的意义完全不一样
     */
    override fun v(tag: String, msg: String, time: Long) {
        list.forEach {
            if (checkNeedLog(LogLevel.VERBOSE, tag, time, it.getLogFilterInfo())) {
                it.v(tag, msg, System.currentTimeMillis())
            }
        }
    }


    override fun d(tag: String, msg: String, time: Long) {
        list.forEach {
            if (checkNeedLog(LogLevel.DEBUG, tag, time, it.getLogFilterInfo())) {
                it.d(tag, msg, System.currentTimeMillis())
            }
        }
    }

    //time "2025/7/18/16:42"
    fun d(tag: String, msg: String, time: String) {
        list.forEach {
            if (checkNeedLog(LogLevel.DEBUG, tag, time, it.getLogFilterInfo())) {
                it.d(tag, msg, System.currentTimeMillis())
            }
        }
    }


    override fun i(tag: String, msg: String, time: Long) {
        list.forEach {
            if (checkNeedLog(LogLevel.INFO, tag, time, it.getLogFilterInfo())) {
                it.i(tag, msg, System.currentTimeMillis())
            }
        }
    }

    override fun w(tag: String, msg: String, time: Long) {
        list.forEach {
            if (checkNeedLog(LogLevel.WARN, tag, time, it.getLogFilterInfo())) {
                it.w(tag, msg, System.currentTimeMillis())
            }
        }
    }

    override fun e(tag: String, msg: String, time: Long) {
        list.forEach {
            if (checkNeedLog(LogLevel.ERROR, tag, time, it.getLogFilterInfo())) {
                it.e(tag, msg, System.currentTimeMillis())
            }
        }
    }

    override fun cancel() {
        list.forEach {
            it.cancel()
        }
    }

    override fun flushLogs() {
        list.forEach {
            it.flushLogs()
        }
    }


    //data class LogFilterInfo(var keyStr: String?, var level: Int=-1, var startTime: Long=-1L, var endTime: Long=-1L)
    private fun checkNeedLog(
        level: Int,
        tag: String,
        time: Long,
        filter: LogFilterInfo?
    ): Boolean {
        if (filter == null) {
            return true;
        }
        if (filter.keyStr != null && !tag.contains(filter.keyStr!!)) {
            return false
        }
        if (level < filter.level) {
            return false
        }
        if (time != -1L) {
            if (time < filter.startTime) {
                return false
            }
            if (filter.endTime != -1L && time > filter.endTime) {
                return false
            }
        }
        return true
    }


    private fun checkNeedLog(
        level: Int,
        tag: String,
        timeStr: String,
        filter: LogFilterInfo?
    ): Boolean {
        if (filter == null) {
            return true;
        }
        if (filter.keyStr != null && !tag.contains(filter.keyStr!!)) {
            return false
        }
        if (level < filter.level) {
            return false
        }
        val time = DataUtils.dateTimeStrToMillis(timeStr, "yyyy/M/dd/HH:mm")
        if (time != -1L) {
            if (time < filter.startTime) {
                return false
            }
            if (filter.endTime != -1L && time > filter.endTime) {
                return false
            }
        }
        return true
    }

}