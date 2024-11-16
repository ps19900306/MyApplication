package com.nwq.opencv.point_rule

import com.nwq.baseobj.CoordinatePoint

interface IPR {
    fun getCoordinatePoint(): CoordinatePoint

    fun checkIpr(src: Any, offsetX: Int = 0, offsetY: Int = 0): Boolean
}