package com.nwq.loguitls

import com.nwq.loguitls.file.FileLog

interface ILog {

    fun v(tag: String, msg: String, time: Long =0L)
    fun d(tag: String, msg: String, time: Long =0L)
    fun i(tag: String, msg: String, time: Long =0L)
    fun w(tag: String, msg: String, time: Long =0L)
    fun e(tag: String, msg: String, time: Long =0L)

    fun cancel()
    fun flushLogs()

    fun getLogFilterInfo(): LogFilterInfo? {
        return null;
    }


}