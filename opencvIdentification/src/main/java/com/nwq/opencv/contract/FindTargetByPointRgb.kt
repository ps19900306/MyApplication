package com.nwq.opencv.contract

import android.graphics.Bitmap
import com.nwq.baseobj.Area

class FindTargetByPointRgb() : FindTarget {

    override fun findTarget(bitmap: Any): Area?{
        if (bitmap is Bitmap)
            return findTargetBitmap(bitmap)
        return null
    }

    override fun release() {

    }


    fun findTargetBitmap(bitmap: Bitmap): Area?{

        return null
    }

}