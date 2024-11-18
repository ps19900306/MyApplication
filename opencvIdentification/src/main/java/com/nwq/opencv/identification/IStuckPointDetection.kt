package com.nwq.opencv.identification

interface IStuckPointDetection {

    //未有变化的次数
    suspend fun checkStuckPoint(): Int


    //
    fun resetCount()



}