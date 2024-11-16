package com.nwq.opencv.rgb

import android.graphics.Bitmap
import com.nwq.baseobj.CoordinatePoint
import com.nwq.opencv.point_rule.BIPR
import kotlin.math.abs

class DifferencePointRule(
    val redDifference: Int,
    val greenDifference: Int,
    val blueDifference: Int,
) {

    companion object {
        private val list = mutableListOf<DifferencePointRule>()
        private const val dFloat =0.4
        fun getSimple(
            red: Int,
            green: Int,
            blue: Int,
        ): DifferencePointRule {
            return list.find {
                it.redDifference == abs(red) && it.greenDifference == abs(green) && it.blueDifference == abs(
                    blue
                )
            } ?: DifferencePointRule(
                Math.abs(red),
                Math.abs(green),
                Math.abs(blue),
            ).apply {
                list.add(this)
            }
        }
    }


    fun verificationRule(
        red1: Int, green1: Int, blue1: Int, red2: Int, green2: Int, blue2: Int
    ): Boolean {
        val flag1 = when {
            redDifference > 0 -> {
                if (red1 > redDifference) {
                    red1 - red2 >= redDifference
                } else {
                    red1 - red2 >= red1 * dFloat
                }
            }

            redDifference == 0 -> true
            else -> { // redDifference < 0
                if (red2 > -redDifference) {
                    red1 - red2 <= redDifference
                } else {
                    red1 - red2 <= red2 * dFloat
                }
            }
        }

        if (!flag1) return false

        val flag2 = when {
            greenDifference > 0 -> {
                if (green1 > greenDifference) {
                    green1 - green2 >= greenDifference
                } else {
                    green1 - green2 >= green1 * dFloat
                }
            }

            greenDifference == 0 -> true
            else -> { // greenDifference < 0
                if (green2 > -greenDifference) {
                    green1 - green2 <= greenDifference
                } else {
                    green1 - green2 <= green2 * dFloat
                }
            }
        }

        if (!flag2) return false
        val flag3 = when {
            blueDifference > 0 -> {
                if (blue1 > blueDifference) {
                    blue1 - blue2 >= blueDifference
                } else {
                    blue1 - blue2 >= blue1 * dFloat
                }
            }

            blueDifference == 0 -> true
            else -> { // blueDifference < 0
                if (blue2 > -blueDifference) {
                    blue1 - blue2 <= blueDifference
                } else {
                    blue1 - blue2 <= blue2 * dFloat
                }
            }
        }
        return flag1 && flag2 && flag3
    }
}