package com.nwq.opencv.opt


import com.nwq.baseutils.MatUtils
import com.nwq.opencv.AutoHsvRuleType
import com.nwq.opencv.db.entity.AutoRulePointEntity
import org.opencv.core.Mat

//对图像进行二值话操作
class BinarizationByGray(val min: Int, val max: Int) : MatResult {
    override fun performOperations(srcMat: Mat): Mat {
        return MatUtils.thresholdByRange(srcMat, min, max);
    }

    override fun requireMatType(): Int {
        return OptStep.MAT_TYPE_GRAY
    }


}