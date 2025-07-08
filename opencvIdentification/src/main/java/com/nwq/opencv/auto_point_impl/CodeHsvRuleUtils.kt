package com.nwq.opencv.auto_point_impl

import com.nwq.opencv.IAutoRulePoint

object CodeHsvRuleUtils {

    public val mAutoRulePointList: List<IAutoRulePoint> =
        listOf(KeyPointImpl(), HighLightAutoPointImpl(), HighLightAutoPointBlackImpl())
}