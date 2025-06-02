package com.nwq.opencv

import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.CoordinatePoint
import com.nwq.imgtake.ImgTake
import com.nwq.opencv.db.entity.TargetVerifyResult

interface IFindTarget {

    val imgTake: ImgTake
        get() = ImgTake.imgTake

    suspend fun findTarget(): CoordinateArea?

    fun release()

    suspend fun checkVerifyResult(): TargetVerifyResult?

    suspend fun getOffsetPoint(): CoordinatePoint //找到的图片和初始位置的偏移值

}