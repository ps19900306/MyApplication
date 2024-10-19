package com.nwq.opencv.identification

import android.graphics.Bitmap
import com.nwq.baseutils.Mat2ArrayUtils
import org.opencv.android.Utils
import org.opencv.calib3d.Calib3d
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.features2d.BFMatcher
import org.opencv.features2d.Features2d
import org.opencv.features2d.ORB
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc


class CvIdentifyCore {

    fun test(){
        // 调用函数
        val (processedImageB, maskB) = preprocessImageB("path_to_imageB.png")
        val result = findImageBInImageA("path_to_imageA.png", processedImageB, maskB)
    }


    fun preprocessImageB(imageBPath: String): Pair<Mat, Mat> {
        // 读取图B
        val imageB = Imgcodecs.imread(imageBPath, Imgcodecs.IMREAD_UNCHANGED)

        // 分离图B的通道，获取Alpha通道
        val rgba = ArrayList<Mat>(4)
        Core.split(imageB, rgba)

        // Alpha通道作为遮罩
        val alphaChannel = rgba[3]

        // 二值化处理，生成遮罩
        val mask = Mat()
        Imgproc.threshold(alphaChannel, mask, 128.0, 255.0, Imgproc.THRESH_BINARY)

        // 使用遮罩来提取图B的有效区域
        val processedImageB = Mat()
        Core.bitwise_and(imageB, imageB, processedImageB, mask)

        return Pair(processedImageB, mask)
    }

    //如果我希望将ImgB的descriptorsB 存入Room数据库，希望存到数据库中，那么我应该如何处理？
    fun findImageBInImageA(imageAPath: String, imageB: Mat, maskB: Mat): Mat {
        val imageA = Imgcodecs.imread(imageAPath, Imgcodecs.IMREAD_COLOR)

        // 创建ORB检测器
        val orb = ORB.create()

        // 查找图A和图B的关键点和描述符
        val keypointsA = MatOfKeyPoint()
        val descriptorsA = Mat()
        orb.detectAndCompute(imageA, Mat(), keypointsA, descriptorsA)

        val keypointsB = MatOfKeyPoint()
        val descriptorsB = Mat()
        orb.detectAndCompute(imageB, maskB, keypointsB, descriptorsB)

        descriptorsB.rows()
        descriptorsB.cols()
        descriptorsB.type()


        Mat2ArrayUtils.matToByteArray(descriptorsB)

        // 使用BFMatcher进行特征点匹配
        val bfMatcher = BFMatcher.create(Core.NORM_HAMMING, true)
        val matches = MatOfDMatch()
        bfMatcher.match(descriptorsB, descriptorsA, matches)



        // 根据匹配结果在图A中绘制图B
        val result = Mat()
        Features2d.drawMatches(imageB, keypointsB, imageA, keypointsA, matches, result)

        return result
    }

    fun findImageRect(imageAPath: String, imageB: Mat, maskB: Mat): Rect? {
        val imageA = Imgcodecs.imread(imageAPath, Imgcodecs.IMREAD_COLOR)

        // 创建ORB检测器
        val orb = ORB.create()

        // 查找图A和图B的关键点和描述符
        val keypointsA = MatOfKeyPoint()
        val descriptorsA = Mat()
        orb.detectAndCompute(imageA, Mat(), keypointsA, descriptorsA)

        val keypointsB = MatOfKeyPoint()
        val descriptorsB = Mat()
        orb.detectAndCompute(imageB, maskB, keypointsB, descriptorsB)

        // 使用BFMatcher进行特征点匹配
        val bfMatcher = BFMatcher.create(Core.NORM_HAMMING, true)
        val matches = MatOfDMatch()
        bfMatcher.match(descriptorsB, descriptorsA, matches)

        // 对匹配点进行过滤（选择最佳匹配）
        val matchesList = matches.toList().sortedBy { it.distance }.take(50)

        if (matchesList.isEmpty()) {
            return null // 没有找到合适的匹配
        }

        // 获取匹配到的关键点的坐标
        val srcPoints = MatOfPoint2f(*matchesList.map { keypointsB.toArray()[it.queryIdx].pt }.toTypedArray())
        val dstPoints = MatOfPoint2f(*matchesList.map { keypointsA.toArray()[it.trainIdx].pt }.toTypedArray())

        // 计算图B在图A中的透视变换矩阵
        val homography = Calib3d.findHomography(srcPoints, dstPoints, Calib3d.RANSAC, 5.0)

        if (homography.empty()) {
            return null // 无法找到合适的变换
        }

        // 图像B的边界点
        val points = arrayOf(
            Point(0.0, 0.0),
            Point(imageB.cols().toDouble(), 0.0),
            Point(imageB.cols().toDouble(), imageB.rows().toDouble()),
            Point(0.0, imageB.rows().toDouble())
        )

        val srcCorners = MatOfPoint2f(*points)
        val dstCorners = MatOfPoint2f()

        // 计算变换后的坐标
        Core.perspectiveTransform(srcCorners, dstCorners, homography)
        val resultPoints = dstCorners.toArray()

        // 计算边界框
        val x = resultPoints.minOf { it.x }.toInt()
        val y = resultPoints.minOf { it.y }.toInt()
        val w = (resultPoints.maxOf { it.x } - x).toInt()
        val h = (resultPoints.maxOf { it.y } - y).toInt()

        return Rect(x, y, w, h)
    }



}