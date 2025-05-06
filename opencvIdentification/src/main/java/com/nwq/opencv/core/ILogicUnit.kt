package com.nwq.opencv.core

import com.nwq.opencv.constant.LogicJudeResult

interface ILogicUnit {
    suspend fun jude(): Boolean

    //当本次jude()返回为True 时，入本方法  count连续进入次数  Boolean是否进行错误上报
    suspend fun onJude(count: Int): Int


    suspend fun hasChanged(nowLogicUnitList: MutableList<ILogicUnit>)

     fun getKeyId(): Long

     fun getChildrenFunctionId(): Long

     fun getPrioritySort():Int
}