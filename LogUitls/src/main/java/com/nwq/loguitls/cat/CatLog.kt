package com.nwq.loguitls.cat

import android.util.Log
import com.nwq.loguitls.ILog
import com.nwq.loguitls.LogFilterInfo

class CatLog(val log: LogFilterInfo? = null) : ILog {
    override fun v(tag: String, msg: String, time: Long) {
        Log.v(tag, msg)
    }
    override fun getLogFilterInfo(): LogFilterInfo? {
        return log;
    }
    override fun d(tag: String, msg: String, time: Long) {
        Log.d(tag, msg)
    }

    override fun i(tag: String, msg: String, time: Long) {
        Log.i(tag, msg)
    }

    override fun w(tag: String, msg: String, time: Long) {
        Log.w(tag, msg)
    }

    override fun e(tag: String, msg: String, time: Long) {
        Log.e(tag, msg)
    }

    override fun cancel() {

    }

    override fun flushLogs() {

    }
}