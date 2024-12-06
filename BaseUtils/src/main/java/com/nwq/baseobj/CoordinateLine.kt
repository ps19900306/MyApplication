package com.nwq.baseobj

data class CoordinateLine(
    var startP: CoordinatePoint,
    var endP: CoordinatePoint,
    var distance: Int = 1
):ICoordinate {
}