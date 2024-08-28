package com.nwq.contract

import com.nwq.baseobj.Area
import org.opencv.core.Mat

interface FindTargetMat : FindTarget {

    override fun findTarget(any: Any): Area? {
        if (any is Mat)
            return findTargetBitmap(any)
        return null
    }

    fun findTargetBitmap(mat: Mat): Area?


}