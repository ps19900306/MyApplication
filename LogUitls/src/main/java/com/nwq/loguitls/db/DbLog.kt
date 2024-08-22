package com.nwq.loguitls.db

import com.nwq.loguitls.ILog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class DbLog : ILog, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun v(tag: String, msg: String, time: Long) {
        TODO("Not yet implemented")
    }

    override fun d(tag: String, msg: String, time: Long) {
        TODO("Not yet implemented")
    }

    override fun i(tag: String, msg: String, time: Long) {
        TODO("Not yet implemented")
    }

    override fun w(tag: String, msg: String, time: Long) {
        TODO("Not yet implemented")
    }

    override fun e(tag: String, msg: String, time: Long) {
        TODO("Not yet implemented")
    }

    override fun cancel() {
        TODO("Not yet implemented")
    }

    override fun flushLogs() {
        TODO("Not yet implemented")
    }
}