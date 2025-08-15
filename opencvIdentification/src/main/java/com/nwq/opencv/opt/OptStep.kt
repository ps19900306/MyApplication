package com.nwq.opencv.opt

import org.opencv.core.Mat

//所有的操作需要继承此接口
interface OptStep<T> {

    companion object {
        public var MAT_TYPE_ALL = 0
        public var MAT_TYPE_RGB = 1
        public var MAT_TYPE_HSV = 2
        public var MAT_TYPE_GRAY = 3
    }

    fun performOperations(srcMat: Mat, type: Int): T


}