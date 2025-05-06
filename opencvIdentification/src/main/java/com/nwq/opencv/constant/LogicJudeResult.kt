package com.nwq.opencv.constant


import androidx.annotation.IntDef


@IntDef(
    LogicJudeResult.ENABLE_SUB_FUNCTIONS,
    LogicJudeResult.NORMAL,
    LogicJudeResult.COMPLETE,
    LogicJudeResult.TIME_OUT,
    LogicJudeResult.STUCK_POINT_END,
)
annotation class LogicJudeResult {
    companion object {
        const val NORMAL = -1;
        const val COMPLETE = Int.MAX_VALUE  //  完成
        const val TIME_OUT = 2   // 连续进入的次数过多
        const val ENABLE_SUB_FUNCTIONS = 3 //开启子模块
        const val STUCK_POINT_END = 5 // 卡点检测卡住了
    }
}





















































































































































