package com.nwq.baseutils

import org.opencv.core.Mat
import java.nio.ByteBuffer

object Mat2ArrayUtils {

    fun matToByteArray(mat: Mat): ByteArray {
        val size = (mat.total() * mat.elemSize()).toInt()
        val byteBuffer = ByteBuffer.allocate(size)
        mat.get(0, 0, ByteArray(size).also { byteBuffer.put(it) })
        return byteBuffer.array()
    }

    fun byteArrayToMat(bytes: ByteArray, type: Int, rows: Int, cols: Int): Mat {
        val mat = Mat(rows, cols, type)
        mat.put(0, 0, bytes)
        return mat
    }




    //确认登录状态
    //发送或者接受消息
    fun sendMessage(message: String) {
        //发送消息
    }
}