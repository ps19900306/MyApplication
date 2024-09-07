package com.nwq.opencv.contract

import android.graphics.Bitmap
import com.nwq.baseobj.CoordinateArea

interface FindTargetByPointRgb : FindTarget {

    override fun findTarget(bitmap: Any): CoordinateArea?{
        if (bitmap is Bitmap)
            return findTargetBitmap(bitmap)
        return null
    }


    fun findTargetBitmap(bitmap: Bitmap): CoordinateArea?

}