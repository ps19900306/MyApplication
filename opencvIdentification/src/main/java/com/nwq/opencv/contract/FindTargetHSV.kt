package com.nwq.opencv.contract

import android.graphics.Bitmap
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.CoordinateUtils
import com.nwq.opencv.hsv.PointHSVRule
import com.nwq.opencv.rgb.PointRule
import org.opencv.core.Mat

//根据HSV进行匹配
abstract class FindTargetHSV(
    tag: String,
    val prList: List<PointHSVRule>,
    val finArea: CoordinateArea?=null,
    val errorTolerance: Int = 0,
) : FindTarget(tag) {

    private var lastOffsetX: Int = 0
    private var lastOffsetY: Int = 0
    private val originArea by lazy {
        CoordinateUtils.calculateBoundingRectangle(prList.map { it.point })
    }
    private val firstP by lazy {
        prList.first().point
    }


    override suspend fun findTarget(): CoordinateArea? {
        if (prList.isEmpty()) {
            return null
        }
        val srcMat = imgTake.getMat(finArea) ?: return null
        return findTargetBitmap(srcMat)
    }


    //这里返回的区域是基于整个图标的
    private fun findTargetBitmap(mat: Mat): CoordinateArea? {
        return if (checkImgTask(mat)) {
            if (finArea == null) {
                CoordinateArea(lastOffsetX, lastOffsetY, originArea.width, originArea.height)
            } else {
                CoordinateArea(
                    lastOffsetX + finArea.x,
                    lastOffsetY + finArea.y,
                    originArea.width,
                    originArea.height
                )
            }
        } else {
            return null
        }
    }


    protected fun checkImgTask(
        srcMat: Mat,
    ): Boolean {
        for (i in 0 until srcMat.cols()) {
            for (j in 0 until srcMat.rows()) {
                var nowErrorCount = 0
                prList.forEach lit@{
                    if (!it.checkIpr(srcMat, i - firstP.x, j - firstP.y)) {
                        nowErrorCount++
                        if (nowErrorCount > errorTolerance) {
                            return@lit  // 从 lambda 表达式中返回
                        }
                    }
                }
                if (nowErrorCount <= errorTolerance) {
                    lastOffsetX = i
                    lastOffsetY = j
                    return true
                }
            }
        }
        return false
    }


}