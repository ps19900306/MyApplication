package com.nwq.optlib

import org.opencv.core.Mat

//这个接口只是为了表返回值
interface MatResult {
    companion object {
        public var MAT_TYPE_BGR = 1
        public var MAT_TYPE_HSV = 2
        public var MAT_TYPE_GRAY = 3
    }
    fun performOperations(srcMat: Mat, type: Int): Pair<Mat?, Int>
}