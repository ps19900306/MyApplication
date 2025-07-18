package com.nwq.opencv.auto_point_impl

import android.util.Log
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.IAutoRulePoint
import com.nwq.opencv.hsv.HSVRule
import org.opencv.core.Mat
import org.opencv.core.Point

/**
 * 利用特征点获取关键点
 */
class KeyPointImpl() : IAutoRulePoint {

    private val tagStr: String = "特征点识别"
    private val descriptionStr: String = "特征点识别"


    override fun boundaryMinDistance(): Int {
        return 3
    }

    override suspend fun autoPoint(hsvMat: Mat): MutableList<Point> {
        val pointList = mutableListOf<Point>()
        val list = MatUtils.getPointByImageKeyPoint(
            hsvMat
        )
        pointList.addAll( list)
        return pointList
    }

    override fun getTag(): String {
        return tagStr
    }

    override fun getDescriptionInfo(): String {
        return descriptionStr
    }


}