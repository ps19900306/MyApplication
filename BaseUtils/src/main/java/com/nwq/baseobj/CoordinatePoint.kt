package com.nwq.baseobj

import com.nwq.BeCode

data class CoordinatePoint(var x: Int, var y: Int) : ICoordinate, BeCode {

    constructor(x: Double, y: Double) : this(x.toInt(), y.toInt())

    constructor(x: Float, y: Float) : this(x.toInt(), y.toInt())

    override fun codeString(): String {
        return "CoordinatePoint($x, $y)"
    }

    override fun toString(): String {
        return "CoordinatePoint(x=$x, y=$y)"
    }


}