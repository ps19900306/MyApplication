package com.nwq.loguitls

import androidx.annotation.IntDef


@IntDef(LogLevel.VERBOSE, LogLevel.DEBUG, LogLevel.INFO, LogLevel.WARN, LogLevel.ERROR)
annotation class LogLevel {
    companion object {
        const val VERBOSE = 1 //自己随便打的日志会删除
        const val DEBUG = 2  //调试临时用信息
        const val INFO = 3   //普通日志信息
        const val WARN = 4   //重要的信息
        const val ERROR = 5  //异常信息

        fun  values():Array<Int> = arrayOf(VERBOSE,DEBUG,INFO,WARN,ERROR)
    }
}