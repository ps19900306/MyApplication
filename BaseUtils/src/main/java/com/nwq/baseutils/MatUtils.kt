package com.nwq.baseutils

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.nio.ByteBuffer

object MatUtils {

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


    //如果一个色彩空间HSV 根据传入的MaxH MaxS MaxV MinH MinS MinV 来进行过滤，获得MaskMat
    /**
     * 根据传入的HSV最大最小值进行过滤，获得MaskMat
     * @param hsvMat 输入的HSV色彩空间图像
     * @param minH 最小色调值
     * @param maxH 最大色调值
     * @param minS 最小饱和度值
     * @param maxS 最大饱和度值
     * @param minV 最小亮度值
     * @param maxV 最大亮度值
     * @return 过滤后的掩码图像
     */
    fun getMaskMat(hsvMat: Mat, minH: Int, maxH: Int, minS: Int, maxS: Int, minV: Int, maxV: Int): Mat {
        val lowerBound = Scalar(minH.toDouble(), minS.toDouble(), minV.toDouble())
        val upperBound = Scalar(maxH.toDouble(), maxS.toDouble(), maxV.toDouble())
        val maskMat = Mat()
        Core.inRange(hsvMat, lowerBound, upperBound, maskMat)
        return maskMat
    }


//    //根据MASK 获取全部的点
//    fun getPointList(srcMat: Mat): List<Point> {
//        val contours = mutableListOf<MatOfPoint>()
//        val hierarchy = Mat()
//        Imgproc.findContours(srcMat, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)
//        contours.forEach {
//            val moments = Imgproc.moments(it)
//            //val area = moments.get(".moment10") *
//        }
//    }


}