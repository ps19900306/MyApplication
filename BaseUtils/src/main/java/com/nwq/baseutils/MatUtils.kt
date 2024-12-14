package com.nwq.baseutils

import android.graphics.Bitmap
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

object MatUtils {

    const val STORAGE_ASSET_TYPE = 0
    const val STORAGE_EXTERNAL_TYPE = 1

    /**
     * 读取并转换HSV色彩空间的Mat对象
     *
     * 根据指定的类型和文件名，从资产目录或外部存储中读取图像，并将其转换为HSV色彩空间的Mat对象
     *
     * @param type 图像存储类型，决定从哪里读取图像（资产目录或外部存储）
     * @param fileName 图像文件名
     * @return 成功读取并转换后返回Mat对象，否则返回null
     */
    fun readHsvMat(type: Int, fileName: String): Mat? {
        // 根据存储类型处理不同的图像读取逻辑
        when (type) {
            STORAGE_ASSET_TYPE -> {
                // 从资产目录读取位图，如果失败则返回null
                val bitmap = FileUtils.readBitmapFromAsset(fileName) ?: return null
                // 将位图转换为HSV色彩空间的Mat对象
                return bitmapToHsvMat(bitmap)
            }

            STORAGE_EXTERNAL_TYPE -> {
                // 从外部存储读取位图，如果失败则返回null
                val bitmap = FileUtils.readBitmapFromRootImg(fileName) ?: return null
                // 将位图转换为HSV色彩空间的Mat对象
                return bitmapToHsvMat(bitmap)
            }

            else -> {
                // 对于不支持的存储类型，返回null
                return null
            }
        }
    }

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


    /**
     * 检测图像中的角点
     *
     * 该函数通过将图像转换到HSV颜色空间，并根据指定的HSV范围进行二值化，随后使用轮廓检测和腐蚀运算来识别和提取角点
     * 主要应用于计算机视觉任务中，用于识别图像中的关键点
     *
     * @param srcMat 输入的图像矩阵，代表原始图像
     * @param minH HSV中色调的最小值
     * @param maxH HSV中色调的最大值
     * @param minS HSV中饱和度的最小值
     * @param maxS HSV中饱和度的最大值
     * @param minV HSV中明度的最小值
     * @param maxV HSV中明度的最大值
     * @param boundaryMinDistance 边界最小距离，用于过滤靠近图像边界的角点，默认为0
     * @param digits 腐蚀运算核大小，默认为3
     * @return 返回检测到的角点列表
     *
     * 注意：该函数依赖于OpenCV库，用于图像处理操作
     */
    fun getCornerPoint(
        srcMat: Mat,
        minH: Int,
        maxH: Int,
        minS: Int,
        maxS: Int,
        minV: Int,
        maxV: Int,
        boundaryMinDistance: Int = 0,
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

        // 如果指定了边界最小距离，则进一步过滤角点
        if (boundaryMinDistance > 0) {
            filterPoints(cornerPoints, srcMat, boundaryMinDistance)
        }

        // 释放内存
        hsvMat.release()
        binaryMat.release()
        erodedMat.release()
        corners.release()
        hierarchy.release()

        return cornerPoints
    }


    /**
     * 根据给定的距离阈值过滤掉矩阵外的点
     * 此函数的目的是从一个点列表中筛选出那些与矩阵边界距离大于等于指定阈值的点
     * 这在需要对矩阵内部的点进行分析或处理，同时忽略靠近边界的点时特别有用
     *
     * @param points 点列表，表示待过滤的点集合
     * @param mat 图像矩阵，提供了矩阵的宽度和高度信息，用于确定边界
     * @param distance 距离阈值，定义了点到矩阵边界的最小距离
     * @return 过滤后的点列表，仅包含与矩阵边界距离大于等于指定阈值的点
     */
    fun filterPoints(points: List<Point>, mat: Mat, distance: Int): List<Point> {
        // 定义矩阵的左边界
        val left = 0
        // 定义矩阵的右边界，即矩阵的宽度
        val right = mat.cols()
        // 定义矩阵的上边界
        val top = 0
        // 定义矩阵的下边界，即矩阵的高度
        val bottom = mat.rows()
        // 过滤点列表，保留与边界距离大于等于指定阈值的点
        return points.filter { point -> isPointWithinDistance(left, right, top, bottom, point, distance) }
    }


    /**
     * 判断点是否在指定区域范围内
     *
     * 该函数用于检查一个点是否位于一个由左、右、上、下边界定义的矩形区域之内
     * 点的位置由其坐标（x, y）确定，而矩形区域则由其四周边界的坐标定义
     * 此外，函数还考虑了一个距离因素，用于扩展或收缩矩形区域的边界
     * 这对于在一定容差范围内判断点是否接近矩形区域边缘很有用
     *
     * @param left 矩形区域的左边界
     * @param right 矩形区域的右边界
     * @param top 矩形区域的上边界
     * @param bottom 矩形区域的下边界
     * @param point 待检测的点
     * @param distance 扩展或收缩矩形区域边界的距离
     * @return 如果点在扩展后的矩形区域内，则返回true；否则返回false
     */
    fun isPointWithinDistance(
        left: Int,
        right: Int,
        top: Int,
        bottom: Int,
        point: Point,
        distance: Int
    ): Boolean {
        // 判断点是否在扩展后的矩形区域内
        val extendedLeft = left + distance
        val extendedRight = right - distance
        val extendedTop = top + distance
        val extendedBottom = bottom - distance
        return point.x.toInt() in extendedLeft..extendedRight && point.y.toInt() in extendedTop..extendedBottom
    }


    fun bitmapToMat(bitmap: Bitmap,coordinateArea: CoordinateArea? =null): Mat {
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
        return  if (coordinateArea!=null){
            cropMat(mat,coordinateArea)
        }else{
            mat
        }
    }


    fun bitmapToHsvMat(bitmap: Bitmap,coordinateArea: CoordinateArea? =null): Mat {
        // 将 Bitmap 转换为 Mat
        val mat = bitmapToMat(bitmap)
        // 创建一个 HSV 格式的 Mat 对象
        val hsvMat = Mat(mat.size(), CvType.CV_8UC3)
        // 将 RGB 格式的 Mat 转换为 HSV 格式的 Mat
        Imgproc.cvtColor(mat, hsvMat, Imgproc.COLOR_RGB2HSV)
        return  if (coordinateArea!=null){
            cropMat(hsvMat,coordinateArea)
        }else{
            hsvMat
        }
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