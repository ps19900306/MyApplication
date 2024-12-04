package com.nwq.opencv.auto_point_impl

import com.nwq.baseutils.MatUtils
import com.nwq.opencv.IAutoRulePoint
import com.nwq.opencv.hsv.HSVRule
import org.opencv.core.Mat
import org.opencv.core.Point

abstract class AutoPointImpl(val tagStr: String) : IAutoRulePoint {

    abstract fun getHSVRuleList(): List<HSVRule>

    override suspend fun autoPoint(hsvMat: Mat): List<Point> {
        val pointList = mutableListOf<Point>()
        getHSVRuleList().forEach {
            val list =
                MatUtils.getCornerPoint(
                    hsvMat,
                    it.minH,
                    it.maxH,
                    it.minS,
                    it.maxS,
                    it.minV,
                    it.maxV
                )
            pointList.addAll(list)
        }
        return pointList
    }

    override fun getTag(): String {
        return tagStr
    }
}