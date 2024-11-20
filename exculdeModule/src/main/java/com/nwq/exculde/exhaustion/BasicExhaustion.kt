package com.nwq.exculde.exhaustion



/**
create by: 86136
create time: 2023/9/1 9:25
Function description:
这个是基础类也是
这个类是为了尽可能的模拟操作 规避检测
 */
open class BasicExhaustion {

    protected val FULL_STATE = 1
    protected val SLIGHT_EXHAUSTION = 2
    protected val SOME_EXHAUSTION = 4
    protected val VERY_EXHAUSTING = 8 //触发这个点的时候就必须要休息
    protected val NEED_REST = 16 //触发这个点的时候就必须要休息

    open fun updateStatus() {

    }

    open fun whetherToRest(): Long {
        return 0L
    }

    open fun getOptInterval(): Int {
        return OptInterval.PRECISION
    }

    open fun getClickDuration(int: Int = OptDuration.QUICK): Long {
        return ((Math.random() * 2 + 1) * 40).toLong()
    }

    open fun getClickInterval(): Long {
        return ((Math.random() * 2 + 1) * 400).toLong()
    }

    open fun getRestTime(): Long {
        return 0L
    }

    open fun onRestComplete() {}


    open fun getSwipDuration(type: Int): Long {
        return ((Math.random() * 2 + 1) * 80).toLong()
    }
}