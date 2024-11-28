package com.nwq.opencv.find_target

import com.nwq.baseobj.CoordinateArea
import com.nwq.imgtake.ImgTake


abstract class FindTarget(val tag: String, val targetOriginalArea: CoordinateArea) {


    protected val imgTake: ImgTake
        get() = ImgTake.imgTake

    abstract suspend fun findTarget(): CoordinateArea?

    abstract fun release()
}