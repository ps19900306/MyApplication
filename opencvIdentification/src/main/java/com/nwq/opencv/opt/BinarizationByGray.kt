package com.nwq.opencv.opt


import android.util.Log
import com.nwq.baseutils.MatUtils
import com.nwq.loguitls.L
import com.nwq.opencv.AutoHsvRuleType
import com.nwq.opencv.db.entity.AutoRulePointEntity
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

//对图像进行二值话操作
class BinarizationByGray(val min: Int, val max: Int) : MatResult {


    override fun performOperations(srcMat: Mat, type: Int): Pair<Mat?, Int> {
        val grayMat = if (type == OptStep.MAT_TYPE_GRAY) {
            srcMat
        } else if (type == OptStep.MAT_TYPE_BGR) {
            MatUtils.bgr2Gray(srcMat)
        } else if (type == OptStep.MAT_TYPE_HSV) {
            MatUtils.hsv2Gray(srcMat)
        } else {
            Log.i("MatResult", "BinarizationByGray::不支持的类型")
            return Pair(null, type)
        }
        return Pair(MatUtils.thresholdByRange(grayMat, min, max), OptStep.MAT_TYPE_GRAY)
    }


}