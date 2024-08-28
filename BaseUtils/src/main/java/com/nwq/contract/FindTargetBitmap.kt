package com.nwq.contract

import android.graphics.Bitmap
import com.nwq.baseobj.Area

interface FindTargetBitmap : FindTarget {

    override fun findTarget(bitmap: Any): Area?{
        if (bitmap is Bitmap)
            return findTargetBitmap(bitmap)
        return null
    }


    fun findTargetBitmap(bitmap: Bitmap): Area?

}