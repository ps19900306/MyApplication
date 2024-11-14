package com.nwq.exculde

import com.nwq.imgtake.ImgTake

//这是一个功能模块 根据实际情况划分粒度
abstract class FunctionUnit {
    abstract val TAG:String
    private val imgTake = ImgTake.imgTake
}