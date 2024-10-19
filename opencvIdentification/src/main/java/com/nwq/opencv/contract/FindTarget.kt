package com.nwq.opencv.contract

import com.nwq.baseobj.CoordinateArea


abstract class FindTarget(val tag: String) {

    abstract fun findTarget(any: Any): CoordinateArea?

    abstract fun release()
}