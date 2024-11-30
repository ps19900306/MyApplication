package com.nwq.opencv.find_target

import com.nwq.baseobj.CoordinateArea
import com.nwq.imgtake.ImgTake

@Deprecated("弃用 准备用数据库的")
abstract class FindTarget(val tag: String, val targetOriginalArea: CoordinateArea) {


    companion object {
        const val HSV_POINT_TYPE = 0
    }

    protected val imgTake: ImgTake
        get() = ImgTake.imgTake

    abstract suspend fun findTarget(): CoordinateArea?

    abstract fun release()
}