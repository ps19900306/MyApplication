package com.nwq.opencv.contract

import android.graphics.Bitmap
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.CommonCallBack
import com.nwq.opencv.db.IdentifyDatabase
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.features2d.Feature2D
import org.opencv.features2d.ORB
import org.opencv.imgproc.Imgproc

abstract class FindTargetImg(tag: String, val bitmapTake: CommonCallBack<Bitmap>) :
    FindTarget(tag) {


    companion object {
        const val IMG_SUFFIX = "_img"
    }
    
    override fun findTarget(bitmap: Any): CoordinateArea? {
        if (bitmap is Bitmap)
            return findTargetBitmap(bitmap)
        return null
    }

    private val templateMat by lazy {
        val templateMat = Mat()
        Utils.bitmapToMat(bitmapTake.callBack(), templateMat)
        templateMat
    }


    fun findTargetBitmap(sourceBitmap: Bitmap): CoordinateArea? {
        // 将 Bitmap 转换为 OpenCV Mat
        val sourceMat = Mat()
        Utils.bitmapToMat(sourceBitmap, sourceMat)


        // 创建输出结果 Mat，大小为 (source - template + 1)
        val resultCols = sourceMat.cols() - templateMat.cols() + 1
        val resultRows = sourceMat.rows() - templateMat.rows() + 1
        val resultMat = Mat(resultRows, resultCols, CvType.CV_32FC1)

        // 执行模板匹配
        Imgproc.matchTemplate(sourceMat, templateMat, resultMat, Imgproc.TM_CCOEFF_NORMED)

        // 寻找最大匹配值和它的对应位置
        val minMaxLocResult = Core.minMaxLoc(resultMat)
        val matchLoc = minMaxLocResult.maxLoc  // 匹配到的最大值位置（最可能的匹配区域）

        // 如果找到合适的匹配区域，返回矩形区域
        if (minMaxLocResult.maxVal >= 0.8) {  // 假设匹配度大于 0.8 认为找到
            val coordinateArea = CoordinateArea(
                matchLoc.x.toInt(), matchLoc.y.toInt(),
                templateMat.width(), templateMat.height()
            )
            return coordinateArea
        }
        // 否则返回 null
        return null
    }


}