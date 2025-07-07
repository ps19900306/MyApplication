package com.nwq.opencv.auto_point_impl

import android.util.Log
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.IAutoRulePoint
import com.nwq.opencv.hsv.HSVRule
import org.opencv.core.Mat
import org.opencv.core.Point

class HighLightAutoPointImpl() : IAutoRulePoint {

    private val tagStr: String = "高亮区域"
    private val descriptionStr: String = "高亮区域"

    // 获取高亮区域
    private val mHighSvRule by lazy {
        val list = mutableListOf<HSVRule>()
        for (i in 0..175 step 5) {
            val rule = HSVRule(i, i + 5, 195, 255, 195, 255)
            list.add(rule)
        }
        list
    }

    //次级高亮区域
    private val mSecondaryHSvRule by lazy {
        val list = mutableListOf<HSVRule>()
        for (i in 0..175 step 5) {
            val rule = HSVRule(i, i + 5, 135, 195, 135, 255)
            list.add(rule)
        }
        list
    }

    override fun boundaryMinDistance(): Int {
        return 3
    }

    override suspend fun autoPoint(hsvMat: Mat): MutableList<Point> {
        val pointList = mutableListOf<Point>()
        mHighSvRule.forEach {
            val list =
                MatUtils.getPointByImageKeyPoint(
                    hsvMat
                )
            Log.i(
                "AutoPointImpl",
                "minH:${it.minH}, maxH:${it.maxH}, minS:${it.minS}, maxS:${it.maxS}, minV:${it.minV}, maxV:${it.maxV}, list:${list.size}"
            )
            pointList.addAll(list)
            if (pointList.size >= getMaxTakePointNumber()) {
                return pointList
            }
        }

        return pointList
    }

    override fun getTag(): String {
        return tagStr
    }

    override fun getDescriptionInfo(): String {
        return descriptionStr
    }


}