package com.nwq.baseobj

data class CoordinatePoint(var x: Int, var y: Int) {

    constructor(x: Double, y: Double) : this(x.toInt(), y.toInt())
}