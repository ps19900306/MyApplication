package com.android.system.taker.judeimg

import com.nwq.baseobj.CoordinateArea
import com.nwq.exculde.JudeUnit
import com.nwq.opencv.contract.FindTarget

class IsInSpaceStation() : JudeUnit() {

    override val TAG: String = IsInSpaceStation::class.java.simpleName
    val finArea: CoordinateArea? = null
    override val findTargetList: List<FindTarget> = emptyList()


}