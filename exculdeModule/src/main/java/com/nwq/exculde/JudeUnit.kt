package com.nwq.exculde

import com.nwq.baseobj.CoordinateArea


//判断单元 一个执行动作的判断单元
abstract class JudeUnit(val tag: String, val finArea: CoordinateArea?) {
    abstract val TAG: String

}