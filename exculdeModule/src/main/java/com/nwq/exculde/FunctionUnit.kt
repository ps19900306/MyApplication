package com.nwq.exculde


import com.nwq.loguitls.L

//这是一个功能模块 根据实际情况划分粒度
abstract class FunctionUnit {

    companion object {
        const val DEFAULT_MAX_COUNT = 20 * 60 //默认的最大执行次数
    }

    abstract val TAG: String

    open suspend fun startFunction() {
        L.i(TAG, "startFunction")
    }

    open suspend fun endFunction() {
        L.i(TAG, "endFunction")
    }



}