package com.nwq.exculde.exhaustion

import androidx.annotation.IntDef

@IntDef(
    OptSlide.NOT_SLIDE,//不进行滑动

    OptSlide.SLIDE_ONE,//滑动了一次点击范围

    OptSlide.SLIDE_TWO,//滑动了二次点击范围
)
annotation class OptSlide {

    companion object {
        const val NOT_SLIDE = 0

        const val SLIDE_ONE = 1

        const val SLIDE_TWO = 2
    }

}