package com.nwq.baseutils

import android.graphics.Bitmap
import android.util.Log
import com.nwq.baseobj.CoordinateArea
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.nio.ByteBuffer
import kotlin.math.log

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
    fun getFilterMaskMat(
        hsvMat: Mat,
        minH: Int,
        maxH: Int,
        minS: Int,
        maxS: Int,
        minV: Int,
        maxV: Int
    ): Mat {
        Log.d("getMaskMat", "minH:$minH maxH:$maxH minS:$minS maxS:$maxS minV:$minV maxV:$maxV")
        val lowerBound = Scalar(minH.toDouble(), minS.toDouble(), minV.toDouble())
        val upperBound = Scalar(maxH.toDouble(), maxS.toDouble(), maxV.toDouble())
        val maskMat = Mat()
        Core.inRange(hsvMat, lowerBound, upperBound, maskMat)
        return maskMat
    }


    fun filterByHsv(
        srcMat: Mat,
        minH: Int,
        maxH: Int,
        minS: Int,
        maxS: Int,
        minV: Int,
        maxV: Int
    ): Mat {
        val maskMat = getFilterMaskMat(srcMat, minH, maxH, minS, maxS, minV, maxV)

        // 将 maskMat 转换为三通道，以便与 srcMat 兼容
        val maskMat3Channel = Mat()
        Imgproc.cvtColor(maskMat, maskMat3Channel, Imgproc.COLOR_GRAY2BGR)

        // 通过 maskMat3Channel 对原图进行过滤，获取符合颜色空间的图像
        val destMat = Mat()
        Core.bitwise_and(srcMat, maskMat3Channel, destMat)
        return destMat
    }


    fun bitmapToMat(bitmap: Bitmap): Mat {
        // 创建一个 Mat 对象
        val mat = Mat()
        // 使用 OpenCV 的 Utils 类将 Bitmap 转换为 Mat
        Utils.bitmapToMat(bitmap, mat)
        // 如果 Bitmap 是 ARGB_8888 格式，需要将其转换为 RGB 格式 去掉A通道
        if (bitmap.config == Bitmap.Config.ARGB_8888) {
            val rgbMat = Mat()
            Imgproc.cvtColor(mat, rgbMat, Imgproc.COLOR_RGBA2RGB)
            return rgbMat
        }
        return mat
    }

    fun bitmapToHsvMat(bitmap: Bitmap): Mat {
        // 将 Bitmap 转换为 Mat
        val mat = bitmapToMat(bitmap)
        // 创建一个 HSV 格式的 Mat 对象
        val hsvMat = Mat(mat.size(), CvType.CV_8UC3)
        // 将 RGB 格式的 Mat 转换为 HSV 格式的 Mat
        Imgproc.cvtColor(mat, hsvMat, Imgproc.COLOR_RGB2HSV)
        return hsvMat
    }


    fun matToBitmap(srcMat: Mat): Bitmap {
        // 创建一个临时的 Mat 对象，用于存储 ARGB_8888 格式的图像
        val argbMat = Mat()
        // 将 RGB 格式的 Mat 转换为 ARGB_8888 格式
        Imgproc.cvtColor(srcMat, argbMat, Imgproc.COLOR_RGB2RGBA)

        // 创建一个 Bitmap 对象
        val bitmap = Bitmap.createBitmap(argbMat.cols(), argbMat.rows(), Bitmap.Config.ARGB_8888)
        // 使用 OpenCV 的 Utils 类将 Mat 转换为 Bitmap
        Utils.matToBitmap(argbMat, bitmap)
        return bitmap
    }


    fun hsvMatToBitmap(srcMat: Mat): Bitmap {
        // 创建一个临时的 Mat 对象，用于存储 RGB 格式的图像
        val rgbMat = Mat()
        // 将 HSV 格式的 Mat 转换为 RGB 格式的 Mat
        Imgproc.cvtColor(srcMat, rgbMat, Imgproc.COLOR_HSV2RGB)

        // 将 RGB 格式的 Mat 转换为 Bitmap
        return matToBitmap(rgbMat)
    }


    fun cropMat(srcMat: Mat, coordinateArea: CoordinateArea): Mat {
        val dstMat = Mat(coordinateArea.height, coordinateArea.width, srcMat.type())
        srcMat.submat(
            coordinateArea.y,
            coordinateArea.y + coordinateArea.height,
            coordinateArea.x,
            coordinateArea.x + coordinateArea.width
        ).copyTo(dstMat)
        return dstMat

    }
}