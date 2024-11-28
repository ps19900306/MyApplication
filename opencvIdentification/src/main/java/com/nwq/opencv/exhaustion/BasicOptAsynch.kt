package com.nwq.opencv.exhaustion

/**
create by: 86136
create time: 2023/9/1 9:25
Function description:
这个是为了保证操作时间不同步
 */
open class BasicOptAsynch {


    //请求操作
    open suspend fun getOptInfo(): OptAsynchInfo? {
        return null
    }

    //请求操作
    open suspend fun requestedOperation(): Long {
        return Long.MAX_VALUE
    }

    //操作结束进行提交
    open suspend fun operationEnded(): Boolean {
        return true
    }


}