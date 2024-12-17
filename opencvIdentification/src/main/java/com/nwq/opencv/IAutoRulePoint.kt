package com.nwq.opencv

import android.graphics.Bitmap
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

    //获取生成此验证规则时候使用的标准图
    fun getStandardBitmap(): Bitmap? {
        return null
    }


}