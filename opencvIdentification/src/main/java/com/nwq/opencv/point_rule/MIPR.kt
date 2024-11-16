package com.nwq.opencv.point_rule

import org.opencv.core.Mat

abstract class MIPR : IPR {
    override fun checkIpr(src: Any, offsetX: Int, offsetY: Int): Boolean {
        if (src is Mat) {
            return checkBIpr(src, offsetX, offsetY)
        }
        return false
    }

    abstract fun checkBIpr(src: Mat, offsetX: Int = 0, offsetY: Int = 0): Boolean
}