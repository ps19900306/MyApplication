package com.nwq.opencv.rgb


import android.graphics.Bitmap
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.nwq.baseobj.CoordinatePoint
import com.nwq.opencv.data.PointVerifyResult
import com.nwq.opencv.point_rule.BIPR


//单点对应单色
class PointRule(val point: CoordinatePoint, val rule: ColorRule) : BIPR() {


    constructor(x: Int, y: Int, r: Int, g: Int, b: Int) : this(
        CoordinatePoint(x, y),
        ColorRule.getSimple(r, g, b)
    )

    override fun getCoordinatePoint(): CoordinatePoint {
        return point
    }


    override fun checkBIpr(bitmap: Bitmap, offsetX: Int, offsetY: Int): Boolean {
        if (bitmap.width <= point.x + offsetX || bitmap.height <= point.y + offsetY) {
            return false
        }
        val intColor = bitmap.getPixel(point.x + offsetX, point.y + offsetY)
        return rule.optInt(intColor)
    }

     fun checkBIpr(bitmap: Bitmap, offsetX: Int, offsetY: Int,  x: Int, y: Int): PointVerifyResult? {
        if (bitmap.width <= point.x + offsetX || bitmap.height <= point.y + offsetY) {
            return null
        }
        val intColor = bitmap.getPixel(point.x + offsetX, point.y + offsetY)
        val isPass =  rule.optInt(intColor)
         return PointVerifyResult(point.x + offsetX+x,point.y + offsetY+y,intColor.red, intColor.green, intColor.blue,isPass)
    }
}