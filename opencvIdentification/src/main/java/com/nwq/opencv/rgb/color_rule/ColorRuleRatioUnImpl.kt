package com.nwq.opencv.rgb.color_rule

import com.nwq.opencv.rgb.ColorRuleRatioImpl

/**
create by: 86136
create time: 2023/5/12 20:19
Function description:
这个根据色值范围进行规则校验：同时增加比例特征值以求保证颜色的特性
 */

open class ColorRuleRatioUnImpl(
    maxRed: Int, minRed: Int, maxGreen: Int,
    minGreen: Int, maxBlue: Int, minBlue: Int,
    redToGreenMax: Float, redToGreenMin: Float,
    redToBlueMax: Float, redToBlueMin: Float,
    greenToBlueMax: Float, greenToBlueMin: Float,
) : ColorRuleRatioImpl(
    maxRed, minRed, maxGreen,
    minGreen, maxBlue, minBlue,
    redToGreenMax, redToGreenMin,
    redToBlueMax, redToBlueMin,
    greenToBlueMax, greenToBlueMin,
) {


    constructor(
        maxRed: Int, minRed: Int, maxGreen: Int, minGreen: Int, maxBlue: Int, minBlue: Int
    ) : this(
        maxRed, minRed, maxGreen,
        minGreen, maxBlue, minBlue,
        maxRed.toFloat() / minGreen, minRed.toFloat() / maxGreen,
        maxRed.toFloat() / minBlue, minRed.toFloat() / maxBlue,
        maxGreen.toFloat() / minBlue, minGreen.toFloat() / maxBlue,
    )


    override fun verificationRule(red: Int, green: Int, blue: Int): Boolean {
        return !super.verificationRule(red, green, blue)
    }

    companion object {
        private val list = mutableListOf<ColorRuleRatioUnImpl>()

        fun getSimple(
            maxRed: Int, minRed: Int,
            maxGreen: Int, minGreen: Int,
            maxBlue: Int, minBlue: Int,
            redToGreenMax: Float, redToGreenMin: Float,
            redToBlueMax: Float, redToBlueMin: Float,
            greenToBlueMax: Float, greenToBlueMin: Float,
        ): ColorRuleRatioUnImpl {
            return list.find {
                it.maxRed == maxRed && it.minRed == minRed && it.maxGreen == maxGreen && it.minGreen == minGreen && it.maxBlue == maxBlue && it.minBlue == minBlue && it.redToGreenMax == redToGreenMax && it.redToGreenMin == redToGreenMin && it.redToBlueMax == redToBlueMax && it.redToBlueMin == redToBlueMin && it.greenToBlueMax == greenToBlueMax && it.greenToBlueMin == greenToBlueMin
            } ?: ColorRuleRatioUnImpl(
                maxRed,
                minRed,
                maxGreen,
                minGreen,
                maxBlue,
                minBlue,
                redToGreenMax,
                redToGreenMin,
                redToBlueMax,
                redToBlueMin,
                greenToBlueMax,
                greenToBlueMin
            ).apply {
                list.add(this)
            }
        }
    }


}