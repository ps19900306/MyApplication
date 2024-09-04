package com.nwq.opencv.rgb.point_rule

import android.graphics.Bitmap
import com.nwq.opencv.rgb.area.CoordinatePoint
import com.nwq.opencv.rgb.color_rule.ColorRule


interface IPR {

    fun getCoordinatePoint(): CoordinatePoint

    fun getColorRule(): ColorRule?{
        return  null
    }

    fun checkIpr(bitmap: Bitmap,offsetX: Int = 0, offsetY: Int = 0):Boolean
}

