package com.nwq.opencv.rgb.area

import com.nwq.opencv.rgb.SetConstant


/**
create by: 86136
create time: 2023/5/5 10:47
Function description:
 */

open class CoordinateArea(
    var x: Int,
    var y: Int,
    var width: Int,
    var height: Int,
    var isRound: Boolean = false
) {

    val xF by lazy {
        ((SetConstant.uiAdaptation?.getWidthRadio() ?: 1.0) * x).toFloat()
    }

    val yF by lazy {
        ((SetConstant.uiAdaptation?.getHeightRadio() ?: 1.0) * y).toFloat()
    }

    val xI by lazy {
        ((SetConstant.uiAdaptation?.getWidthRadio() ?: 1.0) * x).toInt()
    }

    val yI by lazy {
        ((SetConstant.uiAdaptation?.getHeightRadio() ?: 1.0) * y).toInt()
    }


    fun getCenterPoint(): CoordinatePoint {
        return CoordinatePoint(xI + 0.5 * width, yI + 0.5 * height)
    }

    val xD by lazy {
        (SetConstant.uiAdaptation?.getWidthRadio() ?: 1.0) * x
    }

    val yD by lazy {
        (SetConstant.uiAdaptation?.getHeightRadio() ?: 1.0) * y
    }

    //    var offsetX: Int = 0,
//    var offsetY: Int = 0
    constructor(
        x: Float,
        y: Float,
        endX: Float,
        endY: Float,
        b: Boolean = false
    ) : this(x.toInt(), y.toInt(), (endX - x).toInt(), (endY - y).toInt(), b)


    var constrainedArea: CoordinateArea? = null // 约束区域便是这点击区域不能够超过的范围

    //根据偏差值构造新的找寻任务
    open fun copyOffset(offsetX: Int, offsetY: Int): CoordinateArea {
        return if (constrainedArea != null) {
            val area = constrainedArea!!

            var widthP = width
            var heightP = height
            var startX = if (x + offsetX < area.x) {
                widthP -= area.x - (x + offsetX)
                area.x
            } else {
                x + offsetX
            }

            var startY = if (y + offsetY < area.y) {
                heightP -= (area.y - (y + offsetY))
                area.y
            } else {
                y + offsetY
            }

            widthP = if (startX + widthP > area.x + area.width)
                area.x + area.width - startX else widthP
            heightP = if (startY + heightP > area.y + area.height)
                area.y + area.height - startY else heightP


            CoordinateArea(startX, startY, width, height)


        } else {
            CoordinateArea(x + offsetX, y + offsetY, width, height, isRound)
        }
    }

    override fun toString(): String {
        return "CoordinateArea(x=$x, y=$y, width=$width, height=$height, isRound=$isRound)"
    }


}