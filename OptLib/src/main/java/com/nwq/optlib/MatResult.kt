package com.nwq.optlib

import com.nwq.BeCode
import org.opencv.core.Mat

//这个接口只是为了表返回值
interface MatResult : BeCode {

    companion object {
        const val TYPE_CROP_AREA = 0
        const val TYPE_HSV_FILTER_RULE = 1
        const val TYPE_GRAY_FILTER_RULE = 2
    }

    fun performOperations(srcMat: Mat, type: Int): Pair<Mat?, Int>

    fun getType(): Int

   // fun simpleCodeString(srcMat: Mat, type: Int): String
}