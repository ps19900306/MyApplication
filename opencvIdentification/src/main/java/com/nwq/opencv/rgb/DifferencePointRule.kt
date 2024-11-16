package com.nwq.opencv.rgb

import android.graphics.Bitmap
import com.nwq.baseobj.CoordinatePoint
import com.nwq.opencv.point_rule.BIPR


class DifferencePointRule(
    val point1: CoordinatePoint, val point2: CoordinatePoint, val rule: DifferenceColorRule
) : BIPR() {

    constructor(x1: Int, y1: Int, x2: Int, y2: Int, r: Int, g: Int, b: Int) : this(
        CoordinatePoint(x1, y1),
        CoordinatePoint(x2, y2),
        DifferenceColorRule.getSimple(r, g, b)
    )


    override fun checkBIpr(bitmap: Bitmap, offsetX: Int, offsetY: Int): Boolean {
        if (bitmap.width <= point1.x + offsetX || bitmap.height <= point1.y + offsetY || bitmap.width <= point2.x + offsetX || bitmap.height <= point2.y + offsetY) {
            return false
        }
        val intColor1 = bitmap.getPixel(point1.x + offsetX, point1.y + offsetY)
        val intColor2 = bitmap.getPixel(point2.x + offsetX, point2.y + offsetY)
        return rule.optInt(intColor1, intColor2)
    }

    override fun getCoordinatePoint(): CoordinatePoint {
        return point1
    }
}