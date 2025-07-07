package com.nwq.opencv

import androidx.annotation.IntDef

@IntDef(
    AutoHsvRuleType.RGB,

    AutoHsvRuleType.HSV,

    AutoHsvRuleType.IMG,

    AutoHsvRuleType.MAT,

    AutoHsvRuleType.YOLOV,//
)
annotation class AutoHsvRuleType() {
    companion object {
        const val RGB = 1
        const val HSV = 2
        const val IMG = 3
        const val MAT = 4
        const val YOLOV = 5
    }
}