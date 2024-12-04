package com.nwq.constant

// 时间常量
object ConstantTime {
    var TAKE_SCREEN_DELAY = 2000L
    val screenshotInterval
        get() = (2000 * (Math.random() * 1 + 1.6)).toLong()

}