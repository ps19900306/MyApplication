package com.nwq.opencv.rgb


import android.graphics.Bitmap
import com.nwq.baseobj.CoordinatePoint


//单点对应单色
class PointRule(val point: CoordinatePoint, val rule: ColorRule) {

    fun getCoordinatePoint(): CoordinatePoint {
        return point
    }

    fun getColorRule(): ColorRule {
        return rule
    }


    fun checkIpr(bitmap: Bitmap, offsetX: Int, offsetY: Int): Boolean {
        val intColor = bitmap.getPixel(point.x + offsetX, point.y + offsetY)
        return rule.optInt(intColor)
    }




}