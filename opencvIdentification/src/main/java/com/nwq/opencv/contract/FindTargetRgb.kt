package com.nwq.opencv.contract

import android.graphics.Bitmap
import com.nwq.baseobj.CoordinateArea

abstract class FindTargetRgb(tag: String) : FindTarget(tag) {

    override fun findTarget(bitmap: Any): CoordinateArea? {
        if (bitmap is Bitmap)
            return findTargetBitmap(bitmap)
        return null
    }


    abstract fun findTargetBitmap(bitmap: Bitmap): CoordinateArea?


}