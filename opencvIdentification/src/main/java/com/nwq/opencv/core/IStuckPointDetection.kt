package com.nwq.opencv.core

interface IStuckPointDetection {

    //未有变化的次数
    suspend fun checkStuckPoint(): Int


    //
    fun resetCount()



}