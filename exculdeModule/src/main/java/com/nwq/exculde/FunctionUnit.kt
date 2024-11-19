package com.nwq.exculde


import com.nwq.constant.ConstantTime.screenshotInterval
import com.nwq.loguitls.L
import com.nwq.opencv.identification.IStuckPointDetection
import kotlinx.coroutines.delay

//这是一个功能模块 根据实际情况划分粒度
abstract class FunctionUnit {

    companion object {
        const val DEFAULT_MAX_COUNT = 20 * 60 //默认的最大执行次数

        const val TAG_DEFAULT = "FunctionUnit"
    }

    abstract val TAG: String
    abstract val logicUnitList: List<LogicUnit>
    abstract val maxCount: Int
    var lastLogicUnit: LogicUnit? = null


    suspend fun startFunction() {
        beforeStartFunction()
        var count = maxCount
        var isEnd = false
        do {
            delay(screenshotInterval)
            val nowLogicUnit = logicUnitList.find { it.jude() }
            if (lastLogicUnit != null) {
                if (nowLogicUnit != lastLogicUnit) {
                    lastLogicUnit?.hasChanged(logicUnitList)
                    nowLogicUnit?.onJude(logicUnitList, 0)
                } else {
                    nowLogicUnit?.onJude(logicUnitList, ++count)
                }
                lastLogicUnit = nowLogicUnit
            } else {
                count = 0
                nowLogicUnit?.onJude(logicUnitList, count)
            }
            nowLogicUnit?.let {
                isEnd = it.isEnd()
            }
        } while (count > 0 && !isEnd)

    }


    abstract suspend fun updateImg()

    open suspend fun getIStuckPointDetection(): IStuckPointDetection? {
        return null
    }

    open suspend fun beforeStartFunction() {

    }

    open suspend fun endFunction() {

    }


}