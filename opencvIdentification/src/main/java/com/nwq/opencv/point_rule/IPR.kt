package com.nwq.opencv.point_rule

interface IPR {
    fun checkIpr(src: Any, offsetX: Int = 0, offsetY: Int = 0): Boolean
}