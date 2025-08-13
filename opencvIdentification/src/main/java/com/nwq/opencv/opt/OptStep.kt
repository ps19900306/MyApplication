package com.nwq.opencv.opt

import org.opencv.core.Mat

//所有的操作需要继承此接口
interface OptStep<T> {

    fun performOperations(srcMat: Mat): T
}