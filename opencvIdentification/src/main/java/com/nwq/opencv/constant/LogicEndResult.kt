package com.nwq.opencv.constant


import androidx.annotation.IntDef


@IntDef(
    LogicEndResult.NOT_SET,
    LogicEndResult.ERROR,
    LogicEndResult.NORMAL,
    LogicEndResult.COMPLETE,
    LogicEndResult.TIME_OUT,
)
annotation class LogicEndResult {
    companion object {
        const val NOT_SET = -1 //未设置的异常
        const val ERROR = 1 // 异常
        const val NORMAL = 2  //  正常
        const val COMPLETE = 3   //  完成
        const val TIME_OUT = 4   //  超时

    }
}





















































































































































