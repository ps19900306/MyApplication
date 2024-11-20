package com.nwq.exculde.exhaustion

/**
create by: 86136
create time: 2023/8/23 15:58
Function description:这里是一些默认设置
 */
object SetConstant {




    const val SECOND = 1000L
    const val Hour = 1000L * 60 * 60
    const val MINUTE = 1000L * 60

    const val tenMINUTE = 1000L * 60 * 10
    const val halfHour = 1000L * 60 * 30

    const val NO_CLONE_LOCATION = -1
    const val BASE_CLONE_LOCATION = 0



    //这里是点击的间隔
    val clickDuration get() = ((Math.random() * 2 + 1) * 40).toLong()

//    val fastClickInterval
//        get() = (STANDARD_CLICK_INTERVAL * (Math.random() * 0.4 + 0.4)).toLong()

}