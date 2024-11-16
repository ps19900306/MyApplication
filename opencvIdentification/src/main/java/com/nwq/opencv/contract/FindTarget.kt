package com.nwq.opencv.contract

import com.nwq.baseobj.CoordinateArea
import com.nwq.imgtake.ImgTake


abstract class FindTarget(val tag: String) {


    protected val imgTake: ImgTake
        get() = ImgTake.imgTake

    abstract suspend fun findTarget(): CoordinateArea?

    abstract fun release()
}