package com.nwq.opencv

import com.nwq.baseobj.CoordinateArea
import com.nwq.imgtake.ImgTake
import com.nwq.opencv.db.entity.TargetVerifyResult

interface IFindTarget {

    val imgTake: ImgTake
        get() = ImgTake.imgTake

    suspend fun findTarget(): CoordinateArea?

    fun release()

    suspend fun checkVerifyResult(target: CoordinateArea): TargetVerifyResult?

}