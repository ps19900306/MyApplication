package com.nwq.opencv

import androidx.annotation.IntDef

@IntDef(
    FindTargetType.RGB,

    FindTargetType.HSV,

    FindTargetType.IMG,

    FindTargetType.MAT,

    FindTargetType.YOLOV,//
)
annotation class FindTargetType() {
    companion object {
        const val RGB = 1
        const val HSV = 2
        const val IMG = 3
        const val MAT = 4
        const val YOLOV = 5
    }
}