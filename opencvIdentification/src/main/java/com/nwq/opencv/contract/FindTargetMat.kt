package com.nwq.opencv.contract

import com.nwq.baseobj.CoordinateArea
import org.opencv.core.Mat

interface FindTargetMat : FindTarget {


    override fun findTarget(any: Any): CoordinateArea? {
        if (any is Mat)
            return findTargetBitmap(any)
        return null
    }

    fun findTargetBitmap(mat: Mat): CoordinateArea?


}