package com.nwq.exculde.exhaustion

import androidx.annotation.IntDef

@IntDef(
    OptInterval.PRECISION,//精准操作

    OptInterval.CARELESS,//粗心

    OptInterval.REDUNDANCY,//多余的操作

    OptInterval.BLUNDER,//失误 有机会失败

    OptInterval.FAILURE,//


)
annotation class OptInterval() {
    companion object {
        const val PRECISION = 1
        const val CARELESS = 2
        const val REDUNDANCY = 4
        const val BLUNDER = 8
        const val FAILURE = 16
    }
}


