package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.CoordinateUtils
import com.nwq.baseutils.MaskUtils
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.IFindTarget
import com.nwq.opencv.db.converters.CoordinateAreaConverters
import com.nwq.opencv.db.converters.PointHSVRuleConverters
import com.nwq.opencv.hsv.PointHSVRule
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc


@Entity(tableName = "find_target_img")
data class FindTargetMatEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    //识别标签 比如"主菜单","位置菜单"
    val keyTag: String,

    //进行生成时候选的区域
    val targetOriginalArea: CoordinateArea,

    //这个是找图范围
    var findArea: CoordinateArea? = null,

    //这个文件存放的类型现在考虑放外部存储或者asset文件夹 测试时候考虑为外部 打包时候考虑放asset
    val storageType: Int = 0,

    //生成匹配蒙版的类型
    val maskType: Int = 0,

    ) : IFindTarget {


    // 目标图片的 Mat 对象
    private val targetMat by lazy {
        val templateMat = MatUtils.readHsvMat(storageType, keyTag)
        templateMat
    }

    private val maskMat by lazy {
        MaskUtils.getMaskMat(targetMat,maskType)
    }

    override suspend fun findTarget(): CoordinateArea? {
        val srcMat = imgTake.getHsvMat(findArea) ?: return null
        return findTargetBitmap(srcMat)
    }

    override fun release() {

    }


    private suspend fun findTargetBitmap(sourceMat: Mat): CoordinateArea? {
        targetMat?:return null
        // 创建输出结果 Mat，大小为 (source - template + 1)
        val resultCols = sourceMat.cols() - targetMat!!.cols() + 1
        val resultRows = sourceMat.rows() - targetMat!!.rows() + 1
        val resultMat = Mat(resultRows, resultCols, CvType.CV_32FC1)

        // 执行模板匹配
        if (maskMat != null) {
            Imgproc.matchTemplate(sourceMat, targetMat, resultMat, Imgproc.TM_CCOEFF_NORMED, maskMat)
        }else{
            Imgproc.matchTemplate(sourceMat, targetMat, resultMat, Imgproc.TM_CCOEFF_NORMED)
        }
        // 寻找最大匹配值和它的对应位置
        val minMaxLocResult = Core.minMaxLoc(resultMat)
        val matchLoc = minMaxLocResult.maxLoc  // 匹配到的最大值位置（最可能的匹配区域）

        // 如果找到合适的匹配区域，返回矩形区域
        if (minMaxLocResult.maxVal >= 0.8) {  // 假设匹配度大于 0.8 认为找到
            val coordinateArea = CoordinateArea(
                matchLoc.x.toInt(), matchLoc.y.toInt(),
                targetMat!!.width(), targetMat!!.height()
            )
            return coordinateArea
        }
        // 否则返回 null
        return null
    }
}

