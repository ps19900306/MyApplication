package com.nwq.opencv.exhaustion

class OptAsynchNet():BasicOptAsynch() {


    override suspend fun getOptInfo(): OptAsynchInfo? {
        return null
    }

    //请求操作
    override suspend fun requestedOperation(): Long {
        return Long.MAX_VALUE
    }

    //操作结束进行提交
    override suspend fun operationEnded(): Boolean {
        return true
    }
}