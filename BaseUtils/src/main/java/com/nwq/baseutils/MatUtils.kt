package com.nwq.baseutils

import android.graphics.Bitmap
import android.health.connect.datatypes.units.Length
import android.util.Log
import com.nwq.baseobj.CoordinateArea
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.core.Size
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


    /**
     * 根据指定的HSV范围过滤图像
     * 此函数创建一个掩码，该掩码会根据给定的HSV最小值和最大值过滤源图像，然后将过滤后的图像与原始图像进行位与操作，
     * 以提取符合指定HSV范围的图像部分
     *
     * @param srcMat 输入的源图像，预期为HSV色彩空间的Mat对象
     * @param minH 最小色调值
     * @param maxH 最大色调值
     * @param minS 最小饱和度值
     * @param maxS 最大饱和度值
     * @param minV 最小明度值
     * @param maxV 最大明度值
     * @return 返回过滤后的图像，仅包含符合指定HSV范围的部分
     */
    fun filterByHsv(
        srcMat: Mat,
        minH: Int,
        maxH: Int,
        minS: Int,
        maxS: Int,
        minV: Int,
        maxV: Int
    ): Mat {
        // 获取基于指定HSV范围的掩码Mat对象
        val maskMat = getFilterMaskMat(srcMat, minH, maxH, minS, maxS, minV, maxV)
        // 将掩码Mat对象转换为三通道，以便与源图像兼容
        val maskMat3Channel = Mat()
        Imgproc.cvtColor(maskMat, maskMat3Channel, Imgproc.COLOR_GRAY2BGR)

        // 使用掩码对源图像进行过滤，提取符合颜色空间的图像部分
        val destMat = Mat()
        Core.bitwise_and(srcMat, maskMat3Channel, destMat)
        // 返回过滤后的图像
        return destMat
    }


    //通过传入的这些参数对一张图进行得到一张二值化的图,然后对二值化的图像获取轮廓，进行腐蚀操作，然后获取角点坐标并返回
    fun getCornerPoint(
        srcMat: Mat,
        minH: Int,
        maxH: Int,
        minS: Int,
        maxS: Int,
        minV: Int,
        maxV: Int,
        digits: Int = 3 // 腐蚀运算核大小
    ): List<Point> {
        // 确保传入的参数合法
        require(minH in 0..179 && maxH in 0..179 && minH <= maxH) { "Hue 范围非法" }
        require(minS in 0..255 && maxS in 0..255 && minS <= maxS) { "Saturation 范围非法" }
        require(minV in 0..255 && maxV in 0..255 && minV <= maxV) { "Value 范围非法" }
        require(digits > 0) { "腐蚀核大小必须大于 0" }

        // 转换为 HSV 空间
        val hsvMat = Mat()
        Imgproc.cvtColor(srcMat, hsvMat, Imgproc.COLOR_BGR2HSV)

        // 根据阈值进行二值化
        val lowerBound = Scalar(minH.toDouble(), minS.toDouble(), minV.toDouble())
        val upperBound = Scalar(maxH.toDouble(), maxS.toDouble(), maxV.toDouble())
        val binaryMat = Mat()
        Core.inRange(hsvMat, lowerBound, upperBound, binaryMat)

        // 轮廓检测
        val contours = mutableListOf<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(
            binaryMat,
            contours,
            hierarchy,
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        // 创建腐蚀核并进行腐蚀操作
        val kernel = Imgproc.getStructuringElement(
            Imgproc.MORPH_RECT,
            Size(digits.toDouble(), digits.toDouble())
        )
        val erodedMat = Mat()
        Imgproc.erode(binaryMat, erodedMat, kernel)

        // 检测角点
        val corners = MatOfPoint()
        Imgproc.goodFeaturesToTrack(
            erodedMat,
            corners,
            10, // 可设置角点上限
            0.01,
            10.0
        )

        // 将角点结果转换为 List<Point>
        val cornerPoints = mutableListOf<Point>()
        for (i in 0 until corners.rows()) {
            val data = corners.get(i, 0) // 获取单个角点的坐标
            cornerPoints.add(Point(data[0], data[1]))
        }

        // 释放内存
        hsvMat.release()
        binaryMat.release()
        erodedMat.release()
        corners.release()
        hierarchy.release()

        return cornerPoints
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