package com.nwq.opencv.opt


import com.nwq.baseutils.MatUtils
import com.nwq.opencv.AutoHsvRuleType
import com.nwq.opencv.db.entity.AutoRulePointEntity
import org.opencv.core.Mat

//对图像进行二值话操作
class BinarizationByHsvRule(val autoRulePointEntity: AutoRulePointEntity) : MatResult {
    override fun performOperations(srcMat: Mat): Mat {
        var lastMaskMat: Mat? = null
        autoRulePointEntity.prList.forEach { rule ->
            val maskMat = MatUtils.getFilterMaskMat(
                srcMat,
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
        //这里可以直接
        return if (autoRulePointEntity.type == AutoHsvRuleType.RE_FILTER_MASK){
            MatUtils.generateInverseMask(srcMat, lastMaskMat!!)
        }else{
            MatUtils.filterByMask(srcMat, lastMaskMat!!)
        }
    }
}