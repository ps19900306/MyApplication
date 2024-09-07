package com.nwq.opencv.rgb

import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.nwq.baseutils.toRgbInt

/**
 * create by: 86136
 * create time: 2023/5/12 20:19
 * Function description:
 * 这个类根据色值范围进行规则校验，并增加比例特征值以确保颜色的特性。
 */
class ColorRule(
    val maxRed: Int, val minRed: Int, val maxGreen: Int,
    val minGreen: Int, val maxBlue: Int, val minBlue: Int,
    val redToGreenMax: Float, val redToGreenMin: Float,
    val redToBlueMax: Float, val redToBlueMin: Float,
    val greenToBlueMax: Float, val greenToBlueMin: Float,
)  {

    constructor(
        maxRed: Int, minRed: Int, maxGreen: Int, minGreen: Int, maxBlue: Int, minBlue: Int,checkRadio:Boolean = true
    ) : this(
        maxRed, minRed, maxGreen,
        minGreen, maxBlue, minBlue,
        calculateRatio(maxRed,minGreen,checkRadio),
        calculateRatio(minRed,maxGreen,checkRadio),
        calculateRatio(maxRed,minBlue,checkRadio),
        calculateRatio(minRed,maxBlue,checkRadio),
        calculateRatio(maxGreen,minBlue,checkRadio),
        calculateRatio(minGreen,maxBlue,checkRadio)

    )

    fun  optInt(colorInt: Int): Boolean {
        return verificationRule(colorInt.red, colorInt.green, colorInt.blue)
    }


     fun verificationRule(red: Int, green: Int, blue: Int): Boolean {
        val redF = red.toFloat()
        val greenF = green.toFloat()
        val blueF = blue.toFloat()

        val flag1 = if (redToGreenMin > 0 && redToGreenMax > 0 && red > 0 && green > 0) {
            val redToGreen = redF / greenF
            redToGreen in redToGreenMin..redToGreenMax
        } else {
            true
        }

        val flag2 = if (redToBlueMin > 0 && redToBlueMax > 0 && blue > 0 && red > 0) {
            val redToBlue = redF / blueF
            redToBlue in redToBlueMin..redToBlueMax
        } else {
            true
        }

        val flag3 = if (greenToBlueMin > 0 && greenToBlueMax > 0 && blue > 0 && green > 0) {
            val greenToBlue = greenF / blueF
            greenToBlue in greenToBlueMin..greenToBlueMax
        } else {
            true
        }
        val resultFlag =red in minRed..maxRed && green in minGreen..maxGreen && blue in minBlue..maxBlue && flag1 && flag2 && flag3
        return resultFlag
    }



    override fun toString(): String {
        return "ColorRuleRatioImpl(maxRed=$maxRed, minRed=$minRed, maxGreen=$maxGreen, minGreen=$minGreen, maxBlue=$maxBlue, minBlue=$minBlue, redToGreenMax=$redToGreenMax, redToGreenMin=$redToGreenMin, redToBlueMax=$redToBlueMax, redToBlueMin=$redToBlueMin, greenToBlueMax=$greenToBlueMax, greenToBlueMin=$greenToBlueMin)"
    }

    companion object {
        private val cache = mutableMapOf<String, ColorRule>()
        const val NOT_CHECK_RADIO = Float.MIN_VALUE
        const val NOT_CHECK_INT = Int.MIN_VALUE

        fun getSimple(
            maxRed: Int, minRed: Int,
            maxGreen: Int, minGreen: Int,
            maxBlue: Int, minBlue: Int,
            redToGreenMax: Float, redToGreenMin: Float,
            redToBlueMax: Float, redToBlueMin: Float,
            greenToBlueMax: Float, greenToBlueMin: Float,
        ): ColorRule {
            val key =
                "$maxRed-$minRed-$maxGreen-$minGreen-$maxBlue-$minBlue-$redToGreenMax-$redToGreenMin-$redToBlueMax-$redToBlueMin-$greenToBlueMax-$greenToBlueMin"
            return cache.getOrPut(key) {
                ColorRule(
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
                )
            }
        }

        fun getSimple(
            red: Int,
            green: Int,
            blue: Int,
            range: Int = 35,
            rangRatio:Float = 0.2F
        ): ColorRule {

            val maxRed = (red + range).toRgbInt()
            val minRed = (red - range).toRgbInt()
            val maxGreen = (green + range).toRgbInt()
            val minGreen = (green - range).toRgbInt()
            val maxBlue = (blue + range).toRgbInt()
            val minBlue = (blue - range).toRgbInt()

            val (redToGreenMax, redToGreenMin) = calculateRatio(red, green, rangRatio)
            val (redToBlueMax, redToBlueMin) = calculateRatio(red, blue, rangRatio)
            val (greenToBlueMax, greenToBlueMin) = calculateRatio(green, blue, rangRatio)

            return getSimple(
                maxRed, minRed,
                maxGreen, minGreen,
                maxBlue, minBlue,
                redToGreenMax, redToGreenMin,
                redToBlueMax, redToBlueMin,
                greenToBlueMax, greenToBlueMin
            )
        }

        private fun calculateRatio(numerator: Int, denominator: Int, checkRadio: Boolean = true): Float {
            if (denominator == 0 || !checkRadio) NOT_CHECK_RADIO
            return numerator.toFloat() / denominator
        }


        private fun calculateRatio(numerator: Int, denominator: Int, rangRatio: Float = 0F): Pair<Float, Float> {
            if (denominator == 0) return Pair(NOT_CHECK_RADIO, NOT_CHECK_RADIO)
            val fr = numerator.toFloat() / denominator
            return Pair(fr * (1 + rangRatio), fr * (1 - rangRatio))
        }
    }
}
