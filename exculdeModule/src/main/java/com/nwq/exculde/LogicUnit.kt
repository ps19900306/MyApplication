package com.nwq.exculde

import com.nwq.baseobj.CoordinateArea
import com.nwq.opencv.contract.FindTarget


//判断单元 一个执行动作的判断单元
abstract class LogicUnit() {

    abstract val TAG: String
    abstract val findTargetList: List<FindTarget>
    private var lastCoordinateArea: CoordinateArea? = null
    private var clickArea: CoordinateArea? = null
    abstract val errorCount: Int

    suspend fun jude(): Boolean {
        findTargetList.forEach {
            val coordinateArea = it.findTarget()
            if (coordinateArea != null) {
                lastCoordinateArea = coordinateArea
                return true
            }
        }
        return false
    }


    //当本次jude()返回为True 时，入本方法  count连续进入次数  Boolean是否进行错误上报
    suspend fun onJude(nowLogicUnitList: List<LogicUnit>, count: Int): Boolean {
        if (errorCount in 1..<count) {
            return true;
        }
        return false
    }

    //当上一张图jude()返回为True时本张图进进入的不是次方法
    abstract suspend fun hasChanged(nowLogicUnitList: List<LogicUnit>)

    open suspend fun isEnd(): Boolean {
        return false
    }

}

