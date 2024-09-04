package com.nwq.opencv.rgb.point_rule


import android.graphics.Bitmap
import com.nwq.opencv.rgb.area.CoordinatePoint
import com.nwq.opencv.rgb.color_rule.ColorIdentificationRule



//单点对应单色
class PointRule(val point: CoordinatePoint, val rule: ColorIdentificationRule) : IPR {

    override fun getCoordinatePoint(): CoordinatePoint {
        return point
    }

    override fun getColorRule(): ColorIdentificationRule {
        return rule
    }


    override fun checkIpr(bitmap: Bitmap, offsetX: Int, offsetY: Int): Boolean {
        val intColor = bitmap.getPixel(point.xI + offsetX, point.yI + offsetY)
       return rule.optInt(intColor)
    }

}