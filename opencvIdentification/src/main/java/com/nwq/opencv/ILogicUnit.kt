package com.nwq.opencv

interface ILogicUnit {

    //判断是否识别到目标
    suspend fun jude(): Boolean

    //成功识别到目标后 调用次方法  nowLogicUnitList是当前需要识别的所有目标 count连续几次识别为此次目标
    suspend fun onJude(nowLogicUnitList: List<ILogicUnit>, count: Int): Boolean

    //当识别到另外一个目标，且上一次目标是次目标的时候出发此方法
    suspend fun hasChanged(nowLogicUnitList: MutableList<ILogicUnit>)

    //是否结束 如果是结束符则表示
    suspend fun isEnd(): Boolean

    suspend fun getTag(): String

}