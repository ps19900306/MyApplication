package com.nwq.baseobj

import com.nwq.BeCode

data class CoordinateLine(
    var startP: CoordinatePoint,
    var endP: CoordinatePoint,
    var distance: Int = 1
) : ICoordinate, BeCode {
    override fun codeString(): String {
        return "CoordinateLine(${startP.codeString()},${endP.codeString()},$distance)"
    }


}