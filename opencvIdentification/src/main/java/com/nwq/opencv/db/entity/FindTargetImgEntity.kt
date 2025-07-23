package com.nwq.opencv.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.CoordinatePoint
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.AutoHsvRuleType
import com.nwq.opencv.FindTargetType
import com.nwq.opencv.IFindTarget
import com.nwq.opencv.db.IdentifyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc


@Entity(tableName = "find_target_img")
data class FindTargetImgEntity(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    //识别标签 比如"主菜单","位置菜单"
    var keyTag: String,

    //进行生成时候选的区域
    var targetOriginalArea: CoordinateArea,

    //这个是找图范围
    var findArea: CoordinateArea? = null,

    //这个文件存放的类型现在考虑放外部存储或者asset文件夹 测试时候考虑为外部 打包时候考虑放asset
    var storageType: Int = MatUtils.STORAGE_ASSET_TYPE,

    //生成匹配蒙版的类型
    var maskRuleId: Long = -1L,

    var thresholdValue: Float = 0.8f,//这个是找图通过的阈值

    var thresholdValue2: Float = thresholdValue * 0.8f,//这个是找图修正使用的阈值
) : IFindTarget {


    // 目标图片的 Mat 对象
    @Ignore
    private var targetMat: Mat? = null

    private fun getTargetMat(): Mat? {
        if (targetMat == null) {
            targetMat = MatUtils.readHsvMat(storageType, keyTag)
        }
        return targetMat
    }

    @Ignore
    private var maskMat: Mat? = null

    @Ignore
    private val mOffsetPoint: CoordinatePoint = CoordinatePoint(0, 0)


    private suspend fun getMaskMat(): Mat? {
        if (maskRuleId == -1L || targetMat == null)
            return null
        return withContext(Dispatchers.IO) {
            val imgFinalHsvRule =
                IdentifyDatabase.getDatabase().autoRulePointDao().findByKeyId(maskRuleId)
            var lastMaskMat: Mat? = null
            imgFinalHsvRule!!.prList.forEach { rule ->
                val maskMat = MatUtils.getFilterMaskMat(
                    targetMat!!,
                    rule.minH,
                    rule.maxH,
                    rule.minS,
                    rule.maxS,
                    rule.minV,
                    rule.maxV
                )
                if (lastMaskMat == null) {
                    lastMaskMat = maskMat
                } else {
                    lastMaskMat = MatUtils.mergeMaskMat(lastMaskMat!!, maskMat)
                }
            }
            maskMat = if (imgFinalHsvRule!!.type == AutoHsvRuleType.FILTER_MASK) {
                MatUtils.filterByMask(targetMat!!, lastMaskMat!!)
            } else {
                MatUtils.generateInverseMask(targetMat!!, lastMaskMat!!)
            }
            maskMat
        }
    }


    override suspend fun findTarget(): CoordinateArea? {
        val srcMat = imgTake.getHsvMat(findArea) ?: return null
        return findTargetBitmap(srcMat)
    }

    override fun release() {

    }

    override suspend fun checkVerifyResult(): TargetVerifyResult? {
        val sourceMat = imgTake.getHsvMat(findArea) ?: return null
        getTargetMat() ?: return null
        // 创建输出结果 Mat，大小为 (source - template + 1)
        val resultCols = sourceMat.cols() - getTargetMat()!!.cols() + 1
        val resultRows = sourceMat.rows() - getTargetMat()!!.rows() + 1
        val resultMat = Mat(resultRows, resultCols, CvType.CV_32FC1)

        // 执行模板匹配
        if (getMaskMat() != null) {
            Imgproc.matchTemplate(
                sourceMat,
                getTargetMat(),
                resultMat,
                Imgproc.TM_CCOEFF_NORMED,
                getMaskMat()
            )
        } else {
            Imgproc.matchTemplate(sourceMat, getTargetMat(), resultMat, Imgproc.TM_CCOEFF_NORMED)
        }
        // 寻找最大匹配值和它的对应位置
        val minMaxLocResult = Core.minMaxLoc(resultMat)
        val matchLoc = minMaxLocResult.maxLoc  // 匹配到的最大值位置（最可能的匹配区域）

        var coordinateArea: CoordinateArea? = null
        // 如果找到合适的匹配区域，返回矩形区域
        if (minMaxLocResult.maxVal >= thresholdValue2) {  // 假设匹配度大于 0.8 认为找到
            coordinateArea = CoordinateArea(
                matchLoc.x.toInt(), matchLoc.y.toInt(),
                getTargetMat()!!.width(), getTargetMat()!!.height()
            )
        }
        var hasFind = if (minMaxLocResult.maxVal >= thresholdValue) {
            true
        } else {
            false
        }
        return TargetVerifyResult(
            hasFind = hasFind,
            ImgName = keyTag,
            type = FindTargetType.IMG,
            resultArea = coordinateArea,
            nowthreshold = minMaxLocResult.maxVal,
        )
    }

    override suspend fun getOffsetPoint(): CoordinatePoint {
        return mOffsetPoint;
    }


    private suspend fun findTargetBitmap(sourceMat: Mat): CoordinateArea? {
        getTargetMat() ?: return null
        // 创建输出结果 Mat，大小为 (source - template + 1)
        val resultCols = sourceMat.cols() - getTargetMat()!!.cols() + 1
        val resultRows = sourceMat.rows() - getTargetMat()!!.rows() + 1
        val resultMat = Mat(resultRows, resultCols, CvType.CV_32FC1)

        // 执行模板匹配
        if (getMaskMat() != null) {
            Imgproc.matchTemplate(
                sourceMat,
                getTargetMat(),
                resultMat,
                Imgproc.TM_CCOEFF_NORMED,
                getMaskMat()
            )
        } else {
            Imgproc.matchTemplate(sourceMat, getTargetMat(), resultMat, Imgproc.TM_CCOEFF_NORMED)
        }
        // 寻找最大匹配值和它的对应位置
        val minMaxLocResult = Core.minMaxLoc(resultMat)
        val matchLoc = minMaxLocResult.maxLoc  // 匹配到的最大值位置（最可能的匹配区域）

        // 如果找到合适的匹配区域，返回矩形区域
        if (minMaxLocResult.maxVal >= thresholdValue) {  // 假设匹配度大于 0.8 认为找到
            val coordinateArea = CoordinateArea(
                matchLoc.x.toInt(), matchLoc.y.toInt(),
                getTargetMat()!!.width(), getTargetMat()!!.height()
            )
            mOffsetPoint.x = coordinateArea.x - targetOriginalArea.x
            mOffsetPoint.y = coordinateArea.y - targetOriginalArea.y
            return coordinateArea
        }
        // 否则返回 null
        return null
    }
}

