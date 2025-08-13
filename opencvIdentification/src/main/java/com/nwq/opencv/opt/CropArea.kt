package com.nwq.opencv.opt

import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.MatUtils
import org.opencv.core.Mat

//每一个操作在构建的时候就需要
class CropArea(val coordinateArea: CoordinateArea) : MatResult {
    override fun performOperations(srcMat: Mat): Mat {
        return MatUtils.cropMat(srcMat, coordinateArea)
    }

}