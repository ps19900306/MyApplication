package com.nwq.opencv.rgb.color_rule

import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

import com.nwq.opencv.rgb.RGBInfo


//这里定义颜色的校验规则
interface ColorCompareRule: ColorRule {

    fun optInt(colorInt1: Int,colorInt2: Int): Boolean {
        return verificationRule(colorInt1.red, colorInt1.green, colorInt1.blue,colorInt2.red, colorInt2.green, colorInt2.blue)
    }

    fun optRgbInfo(colorInt1: RGBInfo, colorInt2: RGBInfo): Boolean {
        return verificationRule(colorInt1.r, colorInt1.g, colorInt1.b,colorInt2.r, colorInt2.g, colorInt2.b)
    }

    fun verificationRule(red1: Int, green1: Int, blue1: Int, red2: Int, green2: Int, blue2: Int): Boolean





}