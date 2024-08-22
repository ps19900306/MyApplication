package com.nwq.loguitls

import com.nwq.loguitls.file.FileLog

interface ILog {

    fun v(tag: String, msg: String, time: Long)
    fun d(tag: String, msg: String, time: Long)
    fun i(tag: String, msg: String, time: Long)
    fun w(tag: String, msg: String, time: Long)
    fun e(tag: String, msg: String, time: Long)

    fun cancel()
    fun flushLogs()

    fun getLogFilterInfo(): LogFilterInfo? {
        return null;
    }


}