package com.nwq.exculde

import com.nwq.baseobj.CoordinateArea
import com.nwq.exculde.click.ClickArea
import com.nwq.exculde.click.ClickBuilderUtils
import com.nwq.exculde.click.ClickExecuteUtils
import com.nwq.opencv.contract.FindTarget


//判断单元 一个执行动作的判断单元
abstract class LogicUnit(val errorCount: Int = 10) {

    abstract val TAG: String
    abstract val findTargetList: List<FindTarget>
    private var lastCoordinateArea: CoordinateArea? = null
    private var clickArea: ClickArea? = null
    private var nextList: List<LogicUnit>? = null
    private var judeTime = -1
    private var isEnd = false
    suspend fun jude(): Boolean {
        if (judeTime == 0) {
            return false
        }
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
    open suspend fun onJude(nowLogicUnitList: List<LogicUnit>, count: Int): Boolean {
        if (errorCount in 1..<count) {
            return true;
        }
        clickArea?.let { area ->
            if (count % 2 == 1) {
                ClickBuilderUtils.buildClick(lastCoordinateArea!!, area, 0)?.let {
                    ClickExecuteUtils.optClickTask(it)
                }
            }
        }
        return false
    }

    //当上一张图jude()返回为True时本张图进进入的不是次方法
    open suspend fun hasChanged(nowLogicUnitList: MutableList<LogicUnit>) {
        // 如果存在下一个逻辑单元列表，则将其全部添加到当前列表中
        nextList?.let {
            nowLogicUnitList.addAll(it)
        }
        // 如果判断次数大于0，则减少判断次数
        if (judeTime > 0) {
            judeTime--
        }
        // 如果判断次数为0，表明已达到设定的判断次数，从当前列表中移除自身
        if (judeTime == 0) {
            nowLogicUnitList.remove(this)
        }
    }

    open suspend fun isEnd(): Boolean {
        return isEnd
    }

}

