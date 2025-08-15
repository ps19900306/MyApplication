package com.nwq.opencv.opt

import org.opencv.core.Mat

//这个接口只是为了表返回值
interface MatResult : OptStep<Pair<Mat?, Int>> {

    override fun performOperations(srcMat: Mat, type: Int): Pair<Mat?, Int>

}