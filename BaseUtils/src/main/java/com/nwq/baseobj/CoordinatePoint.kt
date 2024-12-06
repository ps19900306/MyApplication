package com.nwq.baseobj

data class CoordinatePoint(var x: Int, var y: Int):ICoordinate {

    constructor(x: Double, y: Double) : this(x.toInt(), y.toInt())

    constructor(x: Float, y: Float) : this(x.toInt(), y.toInt())
    val xF by lazy {
        x.toFloat()
    }


    val yF by lazy {
        y.toFloat()
    }


    val xD by lazy {
        x.toDouble()
    }

    val yD by lazy {
        y.toDouble()
    }
}