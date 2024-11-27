package com.nwq.opencv.hsv

open class HSVRule(
    val minH: Int,
    val maxH: Int,
    val minS: Int,
    val maxS: Int,
    val minV: Int,
    val maxV: Int
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