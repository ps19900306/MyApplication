package com.nwq.opencv.point_rule

import android.graphics.Bitmap

abstract class BIPR : IPR {
    override fun checkIpr(src: Any, offsetX: Int, offsetY: Int): Boolean {
        if (src is Bitmap) {
            return checkBIpr(src, offsetX, offsetY)
        }
        return false
    }

    abstract fun checkBIpr(src: Bitmap, offsetX: Int = 0, offsetY: Int = 0): Boolean
}