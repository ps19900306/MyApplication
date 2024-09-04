package com.nwq.opencv.rgb.area

import com.nwq.opencv.rgb.SetConstant
/**
create by: 86136
create time: 2023/5/5 10:43
Function description:
这里不用INT,这样生成的坐标带小数，触发点击更好用
 */
open class CoordinatePoint(private var x: Float, private var y: Float) {


    constructor(x: Int, y: Int) : this(x.toFloat(), y.toFloat()) {

    }

    constructor(x: Double, y: Double) : this(x.toFloat(), y.toFloat()) {

    }

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


    val xD by lazy {
        (SetConstant.uiAdaptation?.getWidthRadio() ?: 1.0) * x
    }

    val yD by lazy {
        (SetConstant.uiAdaptation?.getHeightRadio() ?: 1.0) * y
    }

    override fun toString(): String {
        return "CoordinatePoint(x=$x, y=$y)"
    }


}