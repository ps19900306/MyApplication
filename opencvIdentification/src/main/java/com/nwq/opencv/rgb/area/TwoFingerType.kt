package com.android.schedule.corelibrary.area

import androidx.annotation.IntDef
import java.lang.annotation.RetentionPolicy

@IntDef(
    TwoFingerType.PULL_OPEN,// 二根手指向外拉

    TwoFingerType.REDUCE_CLOSE,//二根手指向内拉
)
annotation class TwoFingerType() {
    companion object {
        const val PULL_OPEN = 1
        const val REDUCE_CLOSE = 2
    }
}


