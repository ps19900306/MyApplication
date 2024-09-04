package com.nwq.opencv.rgb.color_rule

import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.nwq.opencv.rgb.color_rule.ColorRule

//这里定义颜色的校验规则
interface ColorIdentificationRule: ColorRule {

    fun  optInt(colorInt: Int): Boolean {
        return verificationRule(colorInt.red, colorInt.green, colorInt.blue)
    }

    fun verificationRule(red: Int, green: Int, blue: Int): Boolean


}