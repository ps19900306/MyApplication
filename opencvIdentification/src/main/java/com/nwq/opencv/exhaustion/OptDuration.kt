package com.nwq.opencv.exhaustion

import androidx.annotation.IntDef

@IntDef(
    OptDuration.QUICK,

    OptDuration.SLOW,

    OptDuration.QUICK_LARGE_RANDOM,

    OptDuration.SLOW_LARGE_RANDOM,
)
annotation class OptDuration {

    companion object {
        const val QUICK = 1
        const val SLOW = 2
        const val QUICK_LARGE_RANDOM = 4
        const val SLOW_LARGE_RANDOM = 8
    }

}