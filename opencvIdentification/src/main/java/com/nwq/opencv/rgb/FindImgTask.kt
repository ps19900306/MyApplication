package com.nwq.opencv.rgb

import android.graphics.Bitmap
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.CoordinateUtils
import com.nwq.opencv.contract.FindTargetByPointRgb

class FindImgTask(
    val pointList: List<PointRule>,
    val tag: String,
    val coordinateArea: CoordinateArea = CoordinateUtils.calculateBoundingRectangle(
        pointList.map { it.point })
    ,
    val findArea: CoordinateArea? = null
) : FindTargetByPointRgb {


    override fun findTargetBitmap(bitmap: Bitmap): CoordinateArea? {
        TODO("Not yet implemented")
    }

    override fun release() {
        TODO("Not yet implemented")
    }


}