package com.nwq.baseobj

open class CoordinateArea(val x: Int, val y: Int, val width: Int, val height: Int){

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
}