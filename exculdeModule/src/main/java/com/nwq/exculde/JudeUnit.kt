package com.nwq.exculde

import com.nwq.baseobj.CoordinateArea
import com.nwq.opencv.contract.FindTarget


//判断单元 一个执行动作的判断单元
abstract class JudeUnit() {
    abstract val TAG: String

    abstract val findTargetList: List<FindTarget>


    suspend fun jude(): CoordinateArea? {
        findTargetList.forEach {
            val coordinateArea = it.findTarget()
            if (coordinateArea != null) {
                return coordinateArea
            }
        }
        return null
    }
}