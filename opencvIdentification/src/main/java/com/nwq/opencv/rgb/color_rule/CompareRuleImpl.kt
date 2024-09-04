package com.nwq.opencv.rgb.color_rule

/**
create by: 86136
create time: 2023/5/29 9:44
Function description:
 */

class CompareRuleImpl(
    val maxRed: Int,
    val minRed: Int,
    val maxGreen: Int,
    val minGreen: Int,
    val maxBlue: Int,
    val minBlue: Int,
    val rule1: ColorIdentificationRule? = null,
    val rule2: ColorIdentificationRule? = null,
) : ColorCompareRule {

    override fun verificationRule(
        red1: Int,
        green1: Int,
        blue1: Int,
        red2: Int,
        green2: Int,
        blue2: Int
    ): Boolean {
        val red = red1 - red2
        val green = green1 - green2
        val blue = blue1 - blue2
        return red in minRed..maxRed && green in minGreen..maxGreen && blue in minBlue..maxBlue && (rule1?.verificationRule(
            red1,
            green1,
            blue1
        ) ?: true)
                && (rule2?.verificationRule(red2, green2, blue2) ?: true)
    }

    companion object {
        private val list = mutableListOf<CompareRuleImpl>()

        fun getSimple(
            maxRed: Int,
            minRed: Int,
            maxGreen: Int,
            minGreen: Int,
            maxBlue: Int,
            minBlue: Int,
            rule1: ColorIdentificationRule? = null,
            rule2: ColorIdentificationRule? = null,
        ): CompareRuleImpl {
            return list.find {
                it.maxRed == maxRed && it.minRed == minRed && it.maxGreen == maxGreen && it.minGreen == minGreen && it.maxBlue == maxBlue && it.minBlue == minBlue
                        && it.rule1 == rule1 && it.rule2 == rule2
            } ?: CompareRuleImpl(
                    maxRed,
                    minRed,
                    maxGreen,
                    minGreen,
                    maxBlue,
                    minBlue,
                    rule1,
                    rule2
                ).apply {
                    list.add(this)
                }
        }
    }
}