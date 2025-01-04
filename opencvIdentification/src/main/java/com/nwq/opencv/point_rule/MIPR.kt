package com.nwq.opencv.point_rule

import com.nwq.opencv.data.PointVerifyResult
import org.opencv.core.Mat

abstract class MIPR : IPR {
    override fun checkIpr(src: Any, offsetX: Int, offsetY: Int): Boolean {
        if (src is Mat) {
            return checkBIpr(src, offsetX, offsetY)
        }
        return false
    }

    abstract fun checkBIpr(src: Mat, offsetX: Int = 0, offsetY: Int = 0): Boolean


    abstract fun checkBIpr(src: Mat, offsetX: Int = 0, offsetY: Int = 0,OriginalX : Int = 0, OriginalY : Int = 0): PointVerifyResult?
}