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
    val finArea: CoordinateArea?,
    val errorTolerance: Int = 0,
) : FindTarget(tag) {

    private var lastOffsetX: Int = 0
    private var lastOffsetY: Int = 0
    private val originArea by lazy {
        CoordinateUtils.calculateBoundingRectangle(prList.map { it.point })
    }

    override fun findTarget(mat: Any): CoordinateArea? {
        if (mat is Mat)
            return findTargetBitmap(mat)
        return null
    }


    //这里返回的区域是基于整个图标的
    fun findTargetBitmap(mat: Mat): CoordinateArea? {
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
        if (prList.isEmpty()) {
            return false
        }
        val areaToCheck = finArea?.let { it } ?: CoordinateArea(0, 0, srcMat.cols(), srcMat.rows())
        for (i in areaToCheck.x until areaToCheck.width) {
            for (j in areaToCheck.y until areaToCheck.height) {
                var nowErrorCount = 0
                prList.forEach lit@{
                    if (!it.checkIpr(srcMat, i, j)) {
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