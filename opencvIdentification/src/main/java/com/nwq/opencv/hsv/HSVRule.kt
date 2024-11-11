package com.nwq.opencv.hsv

abstract class HSVRule(
    val minH: Int,
    val maxH: Int,
    val minS: Int,
    val maxS: Int,
    val minV: Int,
    val maxV: Int
) {

    fun verificationRule(h: Int, s: Int, v: Int): Boolean {
        return h in minH..maxH && s in minS..maxS && v in minV..maxV
    }

}