package com.nwq.baseobj

data class CoordinatePoint(var x: Int, var y: Int):ICoordinate {

    constructor(x: Double, y: Double) : this(x.toInt(), y.toInt())

    constructor(x: Float, y: Float) : this(x.toInt(), y.toInt())

    override fun toString(): String {
        return "CoordinatePoint(x=$x, y=$y)"
    }


}