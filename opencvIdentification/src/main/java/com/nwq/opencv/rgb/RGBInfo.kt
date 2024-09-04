package com.nwq.opencv.rgb

import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

/**
create by: 86136
create time: 2023/5/25 20:10
Function description:
这个是对颜色进行分组用的
 */
data class RGBInfo(val r: Int, val g: Int, val b: Int) {
    constructor(colorInt: Int) : this(colorInt.red, colorInt.green, colorInt.blue)
}