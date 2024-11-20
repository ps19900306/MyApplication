package com.nwq.baseobj

class CoordinatePoint(val x: Int, val y: Int) {

    constructor(x: Double, y: Double) : this(x.toInt(), y.toInt())
}