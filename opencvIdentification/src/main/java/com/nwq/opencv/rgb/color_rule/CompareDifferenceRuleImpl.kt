package com.nwq.opencv.rgb.color_rule

import kotlin.math.abs

/**
create by: 86136
create time: 2023/5/29 9:44
Function description:
 */

class CompareDifferenceRuleImpl(
    val redDifference: Int,
    val greenDifference: Int,
    val blueDifference: Int,
) : ColorCompareRule {

    override fun verificationRule(
        red1: Int, green1: Int, blue1: Int, red2: Int, green2: Int, blue2: Int
    ): Boolean {

        val flag1 = abs(red1 - red2) >= redDifference

        val flag2 = abs(green1 - green2) >= greenDifference

        val flag3 = abs(blue1 - blue2) >= blueDifference

        return flag1 && flag2 && flag3
    }

    companion object {
        private val list = mutableListOf<CompareDifferenceRuleImpl>()

        fun getSimple(
            red: Int,
            green: Int,
            blue: Int,
        ): CompareDifferenceRuleImpl {
            return list.find {
                it.redDifference == abs(red) && it.greenDifference ==  abs(green) && it.blueDifference ==  abs(blue)
            } ?: CompareDifferenceRuleImpl(
                Math.abs(red),
                Math.abs(green),
                Math.abs(blue),
            ).apply {
                list.add(this)
            }
        }
    }
}