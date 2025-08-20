package com.nwq.opencv.opt


import android.util.Log
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.AutoHsvRuleType
import com.nwq.opencv.db.entity.AutoRulePointEntity
import org.opencv.core.Mat

//对图像进行二值话操作
class BinarizationByHsvRule(val autoRulePointEntity: AutoRulePointEntity) : MatResult {

    override fun performOperations(srcMat: Mat, type: Int): Pair<Mat?, Int> {
        val hsvMat = if (type == OptStep.MAT_TYPE_HSV) {
            srcMat
        } else if (type == OptStep.MAT_TYPE_BGR) {
            MatUtils.bgr2Hsv(srcMat)
        } else {
            Log.i("MatResult", "BinarizationByGray::不支持的类型")
            return Pair(null, type)
        }

        var lastMaskMat: Mat? = null
        autoRulePointEntity.prList.forEach { rule ->
            val maskMat = MatUtils.getFilterMaskMat(
                hsvMat,
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
                val tempMat = MatUtils.mergeMaskMat(lastMaskMat!!, maskMat)
                lastMaskMat?.release()
                maskMat.release()
                lastMaskMat = tempMat
            }
        }
        //这里可以直接
//        val mat = if (autoRulePointEntity.type == AutoHsvRuleType.RE_FILTER_MASK) {
//            MatUtils.generateInverseMask(hsvMat, lastMaskMat!!)
//        } else {
//            MatUtils.filterByMask(hsvMat, lastMaskMat!!)
//        }
        return Pair(lastMaskMat, OptStep.MAT_TYPE_GRAY)
    }

}