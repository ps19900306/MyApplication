package com.nwq.baseobj

open class CoordinateArea() : ICoordinate {

    public var x: Int = 0
    public var y: Int = 0
    public var width: Int = 0
    public var height: Int = 0
    public var isRound: Boolean = false

    constructor(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        isRound: Boolean = false
    ) : this() {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        this.isRound = isRound
    }

    constructor(
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        isRound: Boolean = false
    ) : this() {
        this.x = x.toInt()
        this.y = y.toInt()
        this.width = width.toInt()
        this.height = height.toInt()
        this.isRound = isRound
    }

    constructor(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        isRound: Boolean = false
    ) : this() {
        this.x = x.toInt()
        this.y = y.toInt()
        this.width = width.toInt()
        this.height = height.toInt()
        this.isRound = isRound
    }

    override fun toString(): String {
        return "CoordinateArea(x=$x, y=$y, width=$width, height=$height, isRound=$isRound)"
    }

    fun toStringSimple(): String {
        return "(x=$x, y=$y, w=$width, h=$height, r=$isRound)"
    }
}