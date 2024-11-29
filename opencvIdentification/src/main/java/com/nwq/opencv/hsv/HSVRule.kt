package com.nwq.opencv.hsv

open class HSVRule(
    var minH: Int,
    var maxH: Int,
    var minS: Int,
    var maxS: Int,
    var minV: Int,
    var maxV: Int
) {


    companion object {
        val allHSVRule = listOf(
            StandardWhiteHSV(),
            StandardGrayHSV(),
            StandardBlackHSV(),
            StandardRedHSV(),
            StandardRed2HSV(),
            StandardOrangeHSV(),
            StandardYellowHSV(),
            StandardGreenHSV(),
            StandardBlueHSV(),
            StandardQingHSV(),
            StandardPurpleHSV(),
            StandardPinkHSV()
        )
    }

    fun verificationRule(h: Int, s: Int, v: Int): Boolean {
        return h in minH..maxH && s in minS..maxS && v in minV..maxV
    }




}