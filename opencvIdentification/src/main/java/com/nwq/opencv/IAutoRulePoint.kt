package com.nwq.opencv

import org.opencv.core.Mat
import org.opencv.core.Point

interface IAutoRulePoint {

    suspend fun autoPoint(hsvMat: Mat): MutableList<Point>

    fun getTag(): String

    fun boundaryMinDistance(): Int {
        return 0
    }

    fun getMaxTakePointNumber(): Int {
        return 30
    }
}