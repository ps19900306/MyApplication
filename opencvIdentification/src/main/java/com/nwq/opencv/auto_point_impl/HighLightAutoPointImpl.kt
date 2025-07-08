package com.nwq.opencv.auto_point_impl

import android.util.Log
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.IAutoRulePoint
import com.nwq.opencv.hsv.HSVRule
import org.opencv.core.Mat
import org.opencv.core.Point

/**
 * 利用高亮区域色块获取关键点
 * 常用颜色块识别
 */
class HighLightAutoPointImpl() : IAutoRulePoint {

    private val tagStr: String = "高亮区域"
    private val descriptionStr: String = "高亮区域"

    //    0<=h<20， 红色
//    30<=h<45， 黄色
//    45<=h<90， 绿色
//    90<=h<125， 青色
//    125<=h<150， 蓝色
//    150<=h<175， 紫色
//    175<=h<200， 粉红色
//    200<=h<220， 砖红色
//    220<=h<255， 品红色
    // 获取高亮区域
    private val mHighSvRule by lazy {
        val list = listOf(
            HSVRule(0, 20, 195, 255, 195, 255),
            HSVRule(30, 45, 195, 255, 195, 255),
            HSVRule(45, 90, 195, 255, 195, 255),
            HSVRule(90, 125, 195, 255, 195, 255),
            HSVRule(125, 150, 195, 255, 195, 255),
            HSVRule(150, 175, 195, 255, 195, 255),
            HSVRule(175, 200, 195, 255, 195, 255),
            HSVRule(200, 220, 195, 255, 195, 255),
            HSVRule(220, 255, 195, 255, 195, 255),
        )
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
                MatUtils.getPointByRange(
                    hsvMat,
                    it.minH,
                    it.maxH,
                    it.minS,
                    it.maxS,
                    it.minV,
                    it.maxV,
                    boundaryMinDistance(),
                    3
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