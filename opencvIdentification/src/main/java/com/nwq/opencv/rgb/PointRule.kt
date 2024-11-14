package com.nwq.opencv.rgb


import android.graphics.Bitmap
import com.nwq.baseobj.CoordinatePoint


//单点对应单色
class PointRule(val point: CoordinatePoint, val rule: ColorRule) {


    constructor(x: Int, y: Int, r: Int, g: Int, b: Int) : this(
        CoordinatePoint(x, y),
        ColorRule.getSimple(r, g, b)
    )


    fun getCoordinatePoint(): CoordinatePoint {
        return point
    }

    fun getColorRule(): ColorRule {
        return rule
    }


    fun checkIpr(bitmap: Bitmap, offsetX: Int, offsetY: Int): Boolean {
        if (bitmap.width <= point.x + offsetX || bitmap.height <= point.y + offsetY) {
            return false
        }
        val intColor = bitmap.getPixel(point.x + offsetX, point.y + offsetY)
        return rule.optInt(intColor)
    }

}