package com.nwq.loguitls

import com.nwq.loguitls.file.FileLog

interface ILog {

    //-1L 则是永久显示的
    fun v(tag: String, msg: String, time: Long = -1L)
    fun d(tag: String, msg: String, time: Long = -1L)
    fun i(tag: String, msg: String, time: Long = -1L)
    fun w(tag: String, msg: String, time: Long = -1L)
    fun e(tag: String, msg: String, time: Long = -1L)

    fun cancel()
    fun flushLogs()

    fun getLogFilterInfo(): LogFilterInfo? {
        return null;
    }


}