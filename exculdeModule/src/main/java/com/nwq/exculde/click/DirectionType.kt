package com.nwq.exculde.click

import androidx.annotation.IntDef
import java.lang.annotation.RetentionPolicy

@IntDef(
    DirectionType.LEFT,//原点必须为右上角

    DirectionType.RIGHT,//原点必须为左上角

    DirectionType.TOP,//原点必须为左边下角

    DirectionType.BOTTOM,//原点必须为左边上角

    DirectionType.TOP_LEFT,//

    DirectionType.TOP_RIGHT,//

    DirectionType.BOTTOM_LEFT,//

    DirectionType.BOTTOM_RIGHT,//

    DirectionType.NONE,//
)
annotation class DirectionType() {
    companion object {
        const val LEFT = 1
        const val TOP_LEFT = 2
        const val TOP = 3
        const val TOP_RIGHT = 4
        const val RIGHT = 5
        const val BOTTOM_RIGHT = 6
        const val BOTTOM = 7
        const val BOTTOM_LEFT = 8
        const val NONE = -1
    }
}


