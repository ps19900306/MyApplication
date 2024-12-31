package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.MaskUtils
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.IFindTarget
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc


@Entity(tableName = "find_target_img")
data class FindTargetImgEntity(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    //识别标签 比如"主菜单","位置菜单"
    var keyTag: String,

    //进行生成时候选的区域
    var targetOriginalArea: CoordinateArea,

    //这个是找图范围
    var findArea: CoordinateArea? = null,

    //这个文件存放的类型现在考虑放外部存储或者asset文件夹 测试时候考虑为外部 打包时候考虑放asset
    var storageType: Int = MatUtils.STORAGE_ASSET_TYPE,

    //生成匹配蒙版的类型
    var maskType: Int = MaskUtils.UN_SET_MASK,

    ) : IFindTarget {


    // 目标图片的 Mat 对象
    @Ignore
    private var targetMat:Mat?=null

    private fun getTargetMat():Mat?{
        if (targetMat == null) {
            targetMat= MatUtils.readHsvMat(storageType, keyTag)
        }
        return targetMat
    }
    @Ignore
    private var maskMat:Mat?=null

    private fun getMaskMat():Mat?{
        if (maskMat == null) {
            maskMat= MaskUtils.getMaskMat(getTargetMat(),maskType)
        }
        return maskMat
    }


    override suspend fun findTarget(): CoordinateArea? {
        val srcMat = imgTake.getHsvMat(findArea) ?: return null
        return findTargetBitmap(srcMat)
    }

    override fun release() {

    }

    override suspend fun checkVerifyResult(target: CoordinateArea): TargetVerifyResult? {
        TODO("Not yet implemented")
    }


    private suspend fun findTargetBitmap(sourceMat: Mat): CoordinateArea? {
        getTargetMat()?:return null
        // 创建输出结果 Mat，大小为 (source - template + 1)
        val resultCols = sourceMat.cols() - getTargetMat()!!.cols() + 1
        val resultRows = sourceMat.rows() - getTargetMat()!!.rows() + 1
        val resultMat = Mat(resultRows, resultCols, CvType.CV_32FC1)

        // 执行模板匹配
        if (getMaskMat() != null) {
            Imgproc.matchTemplate(sourceMat, getTargetMat(), resultMat, Imgproc.TM_CCOEFF_NORMED, getMaskMat())
        }else{
            Imgproc.matchTemplate(sourceMat, getTargetMat(), resultMat, Imgproc.TM_CCOEFF_NORMED)
        }
        // 寻找最大匹配值和它的对应位置
        val minMaxLocResult = Core.minMaxLoc(resultMat)
        val matchLoc = minMaxLocResult.maxLoc  // 匹配到的最大值位置（最可能的匹配区域）

        // 如果找到合适的匹配区域，返回矩形区域
        if (minMaxLocResult.maxVal >= 0.8) {  // 假设匹配度大于 0.8 认为找到
            val coordinateArea = CoordinateArea(
                matchLoc.x.toInt(), matchLoc.y.toInt(),
                getTargetMat()!!.width(), getTargetMat()!!.height()
            )
            return coordinateArea
        }
        // 否则返回 null
        return null
    }
}

