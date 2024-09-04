package com.nwq.opencv.rgb.color_rule

/**
create by: 86136
create time: 2023/5/12 20:19
Function description:
这个根据色值范围进行规则校验
 */

class ColorRuleUnImpl(
    val maxRed: Int,
    val minRed: Int,
    val maxGreen: Int,
    val minGreen: Int,
    val maxBlue: Int,
    val minBlue: Int
) : ColorIdentificationRule {

    constructor(red: Int, green: Int, blue: Int, range: Int) : this(
        red + range,
        red - range,
        green + range,
        green - range,
        blue + range,
        blue - range
    )

    override fun verificationRule(red: Int, green: Int, blue: Int): Boolean {
        return !(red in minRed..maxRed && green in minGreen..maxGreen && blue in minBlue..maxBlue)
    }

    companion object {
        private val list = mutableListOf<ColorRuleUnImpl>()


        fun getSimple(
            maxRed: Int = 255,
            minRed: Int = 0,
            maxGreen: Int = 255,
            minGreen: Int = 0,
            maxBlue: Int = 255,
            minBlue: Int = 0,
        ): ColorRuleUnImpl {
            return list.find { it.maxRed == maxRed && it.minRed == minRed && it.maxGreen == maxGreen && it.minGreen == minGreen && it.maxBlue == maxBlue && it.minBlue == minBlue }
                ?: ColorRuleUnImpl(maxRed, minRed, maxGreen, minGreen, maxBlue, minBlue).apply {
                    list.add(this)
                }
        }
    }

    fun getAllLess(min: Int): ColorRuleUnImpl {
        return getSimple(maxRed = min, maxBlue = min, maxGreen = min)
    }

}