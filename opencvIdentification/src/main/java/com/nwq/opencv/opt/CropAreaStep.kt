package com.nwq.opencv.opt

import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.MatUtils
import org.opencv.core.Mat

//每一个操作在构建的时候就需要
class CropAreaStep(val coordinateArea: CoordinateArea) : MatResult {

    override fun performOperations(srcMat: Mat, type: Int): Pair<Mat, Int> {
        return Pair(MatUtils.cropMat(srcMat, coordinateArea), type)
    }


}