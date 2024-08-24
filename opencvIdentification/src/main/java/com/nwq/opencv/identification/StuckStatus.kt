package com.nwq.opencv.identification

import androidx.annotation.IntDef


@IntDef(StuckStatus.NUMBER_TOO_SMALL, StuckStatus.NORMAL, StuckStatus.IS_STUCK, StuckStatus.IS_STUCK_LONG, StuckStatus.ERROR)
annotation class StuckStatus {
    companion object {
        const val NUMBER_TOO_SMALL = 1 //未想好
        const val NORMAL = 2  //调试临时用信息
        const val IS_STUCK = 3   //普通日志信息
        const val IS_STUCK_LONG = 4   //重要的信息
        const val ERROR = 5  //异常信息

        fun  values():Array<Int> = arrayOf(NUMBER_TOO_SMALL,NORMAL,IS_STUCK,IS_STUCK_LONG,ERROR)
    }
}