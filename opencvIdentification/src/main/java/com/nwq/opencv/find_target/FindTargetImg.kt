package com.nwq.opencv.find_target

import android.graphics.Bitmap
import com.nwq.baseobj.CoordinateArea
import com.nwq.callback.CommonCallBack
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc


//进行图片匹配  需要考虑是否增加 蒙版
abstract class FindTargetImg(
    tag: String,
    srcArea: CoordinateArea,
    val bitmapTake: CommonCallBack<Bitmap>,
    val finArea: CoordinateArea? = null,
) :
    FindTarget(tag,srcArea) {


    // 目标图片的 Mat 对象
    private val targetMat by lazy {
        val templateMat = Mat()
        Utils.bitmapToMat(bitmapTake.callBack(), templateMat)
        templateMat
    }

    override suspend fun findTarget(): CoordinateArea? {
        val srcMat = imgTake.getHsvMat(finArea) ?: return null
        return findTargetBitmap(srcMat)
    }


    private suspend fun findTargetBitmap(sourceMat: Mat): CoordinateArea? {
        // 创建输出结果 Mat，大小为 (source - template + 1)
        val resultCols = sourceMat.cols() - targetMat.cols() + 1
        val resultRows = sourceMat.rows() - targetMat.rows() + 1
        val resultMat = Mat(resultRows, resultCols, CvType.CV_32FC1)

        // 执行模板匹配
        Imgproc.matchTemplate(sourceMat, targetMat, resultMat, Imgproc.TM_CCOEFF_NORMED)

        // 寻找最大匹配值和它的对应位置
        val minMaxLocResult = Core.minMaxLoc(resultMat)
        val matchLoc = minMaxLocResult.maxLoc  // 匹配到的最大值位置（最可能的匹配区域）

        // 如果找到合适的匹配区域，返回矩形区域
        if (minMaxLocResult.maxVal >= 0.8) {  // 假设匹配度大于 0.8 认为找到
            val coordinateArea = CoordinateArea(
                matchLoc.x.toInt(), matchLoc.y.toInt(),
                targetMat.width(), targetMat.height()
            )
            return coordinateArea
        }
        // 否则返回 null
        return null
    }


}