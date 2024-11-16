package com.nwq.callback

interface CommonCallBack2<D, T> {

    fun callBack(data: D): T
}