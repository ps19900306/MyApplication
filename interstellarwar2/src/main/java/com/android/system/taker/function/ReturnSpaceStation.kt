package com.android.system.taker.function

import com.nwq.exculde.FunctionUnit
import com.nwq.exculde.LogicUnit

class ReturnSpaceStation(
    override val logicUnitList: MutableList<LogicUnit>,
    override val maxCount: Int
) : FunctionUnit() {

    override val TAG: String = ReturnSpaceStation::class.java.simpleName




}