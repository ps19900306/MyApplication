package com.nwq.opencv.exhaustion


import kotlinx.coroutines.delay


//这个是为了取时间片 单位都是秒
class OptAsynchTime(val SecondSize: Int, val denominator: Int, val numerator: Int) :
    BasicOptAsynch() {


    //请求操作是否需要进行等待
    override suspend fun getOptInfo(): OptAsynchInfo {
        val timeSecond = (System.currentTimeMillis() / SetConstant.SECOND).toInt()

        val remainder = (timeSecond / SecondSize) % denominator
        val remainderSecond = timeSecond % SecondSize
        if (remainder == numerator) {
            return OptAsynchInfo(0L, (SecondSize - remainderSecond) * SetConstant.SECOND)
        } else if (numerator > remainder) {
            val delayTime = (numerator - remainder) * SecondSize - remainderSecond
            return OptAsynchInfo(delayTime * SetConstant.SECOND, SecondSize * SetConstant.SECOND)
        } else {
            val delayTime = (remainder + denominator - numerator) * SecondSize - remainderSecond
            return OptAsynchInfo(delayTime * SetConstant.SECOND, SecondSize * SetConstant.SECOND)
        }
    }

    //请求操作
    override suspend fun requestedOperation(): Long {
        val timeSecond = (System.currentTimeMillis() / SetConstant.SECOND).toInt()
        val remainder = (timeSecond / SecondSize) % denominator
        val remainderSecond = timeSecond % SecondSize
        if (remainder == numerator) {
            return (SecondSize - remainderSecond) * SetConstant.SECOND
        } else if (numerator > remainder) {
            val delayTime = (numerator - remainder) * SecondSize - remainderSecond
            delay(delayTime * SetConstant.SECOND)
            return SecondSize * SetConstant.SECOND
        } else {
            val delayTime = (remainder + denominator - numerator) * SecondSize - remainderSecond
            delay(delayTime * SetConstant.SECOND)
            return SecondSize * SetConstant.SECOND
        }
    }

    //操作结束进行提交
    override suspend fun operationEnded(): Boolean {
        return true
    }


}