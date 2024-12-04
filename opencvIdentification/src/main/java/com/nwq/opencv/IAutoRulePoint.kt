package com.nwq.opencv

import org.opencv.core.Mat
import org.opencv.core.Point

interface IAutoRulePoint {

    fun autoPoint(hsvMat: Mat):List<Point>

    fun getTag():String

}