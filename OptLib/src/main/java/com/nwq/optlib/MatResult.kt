package com.nwq.optlib

import com.nwq.BeCode
import org.opencv.core.Mat

//这个接口只是为了表返回值
interface MatResult : BeCode {


    fun performOperations(srcMat: Mat, type: Int): Pair<Mat?, Int>

   // fun simpleCodeString(srcMat: Mat, type: Int): String
}