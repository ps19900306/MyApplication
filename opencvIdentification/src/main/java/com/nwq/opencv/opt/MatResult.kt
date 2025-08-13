package com.nwq.opencv.opt

import org.opencv.core.Mat

//这个接口只是为了表返回值
interface MatResult : OptStep<Mat> {

    override fun performOperations(srcMat:Mat): Mat

}