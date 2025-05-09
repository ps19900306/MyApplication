package com.nwq.baseobj

open class CoordinateArea(val x: Int, val y: Int, val width: Int, val height: Int, var isRound: Boolean = false):ICoordinate{


    constructor(x: Double, y: Double, width: Double, height: Double, isRound: Boolean = false) : this(x.toInt(), y.toInt(), width.toInt(), height.toInt(),isRound)
    constructor(x: Float, y: Float, width: Float, height: Float, isRound: Boolean = false) : this(x.toInt(), y.toInt(), width.toInt(), height.toInt(),isRound)

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CoordinateArea) return false

        if (x != other.x) return false
        if (y != other.y) return false
        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + width
        result = 31 * result + height
        return result
    }

    override fun toString(): String {
        return "CoordinateArea(x=$x, y=$y, width=$width, height=$height, isRound=$isRound)"
    }


}