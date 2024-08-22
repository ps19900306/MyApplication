package com.nwq.loguitls

//日志输出的类
object L : ILog {

    private val list = mutableListOf<ILog>()

    override fun v(tag: String, msg: String, time: Long) {
        list.forEach {
            if (checkNeedLog(LogLevel.VERBOSE, tag, time, it.getLogFilterInfo())) {
                it.v(tag, msg, time)
            }
        }
    }

    override fun d(tag: String, msg: String, time: Long) {
        list.forEach {
            if (checkNeedLog(LogLevel.DEBUG, tag, time, it.getLogFilterInfo())) {
                it.d(tag, msg, time)
            }
        }
    }

    override fun i(tag: String, msg: String, time: Long) {
        list.forEach {
            if (checkNeedLog(LogLevel.INFO, tag, time, it.getLogFilterInfo())) {
                it.i(tag, msg, time)
            }
        }
    }

    override fun w(tag: String, msg: String, time: Long) {
        list.forEach {
            if (checkNeedLog(LogLevel.WARN, tag, time, it.getLogFilterInfo())) {
                it.w(tag, msg, time)
            }
        }
    }

    override fun e(tag: String, msg: String, time: Long) {
        list.forEach {
            if (checkNeedLog(LogLevel.ERROR, tag, time, it.getLogFilterInfo())) {
                it.e(tag, msg, time)
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
        if (time < filter.startTime) {
            return false
        }
        if (filter.endTime != -1L && time > filter.endTime) {
            return false
        }
        return true
    }

}