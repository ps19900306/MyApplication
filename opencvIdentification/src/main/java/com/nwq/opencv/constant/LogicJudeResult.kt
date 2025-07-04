package com.nwq.opencv.constant


import androidx.annotation.IntDef
import com.nwq.baseutils.ContextUtils


@IntDef(
    LogicJudeResult.ENABLE_SUB_FUNCTIONS,
    LogicJudeResult.NORMAL,
    LogicJudeResult.COMPLETE,
    LogicJudeResult.FUNCTION_END,
    LogicJudeResult.TIME_OUT,
    LogicJudeResult.STUCK_POINT_END,
    LogicJudeResult.FUNCTION_NOT_FIND
)
annotation class LogicJudeResult {
    companion object {
        const val NORMAL = -1;

        const val TIME_OUT = 2   // 连续进入的次数过多
        const val ENABLE_SUB_FUNCTIONS = 3 //开启子模块
        const val STUCK_POINT_END = 5 // 卡点检测卡住了
        const val FUNCTION_NOT_FIND = 6 // 卡点检测卡住了
        const val FUNCTION_END = Int.MAX_VALUE - 1  //  次功能运行结束
        const val COMPLETE = Int.MAX_VALUE  //  完成,表示不能继续 比如所有任务已经结束



    }


}





















































































































































