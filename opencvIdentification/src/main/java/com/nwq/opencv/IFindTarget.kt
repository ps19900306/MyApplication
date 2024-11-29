package com.nwq.opencv

import com.nwq.baseobj.CoordinateArea
import com.nwq.imgtake.ImgTake

interface IFindTarget {

    val imgTake: ImgTake
        get() = ImgTake.imgTake

    suspend fun findTarget(): CoordinateArea?

    fun release()

}