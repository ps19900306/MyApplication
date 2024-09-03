package com.nwq.opencv


import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc


object ProcessUtils {

    //寻找高亮区域
    fun findHighBrightnessArea (src: Mat) {
        // 转换为灰度图
        val gray = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY)

        // 自适应阈值化
        val binary = Mat()
        Imgproc.adaptiveThreshold(
            gray,
            binary,
            255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY,
            11,
            2.0
        )

        // 形态学操作
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
        Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_OPEN, kernel)

        // 查找轮廓
        val contours: MutableList<MatOfPoint> = mutableListOf()
        val hierarchy = Mat()
        Imgproc.findContours(
            binary,
            contours,
            hierarchy,
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        // 筛选出矩形轮廓
        for (i in contours.indices) {
            val area = Imgproc.contourArea(contours[i])
            if (area > 1000) { // 设置一个最小面积阈值
                val rect: Rect = Imgproc.boundingRect(contours[i])
                val aspectRatio: Double = rect.width.toDouble() / rect.height.toDouble()
                if (aspectRatio >= 0.5 && aspectRatio <= 2) { // 矩形宽高比的范围
                    // 绘制矩形框
                    Imgproc.rectangle(src, rect.tl(), rect.br(), Scalar(0.0, 255.0, 0.0), 2)
                }
            }
        }

        // 显示或保存结果
        Imgcodecs.imwrite("output.jpg", src)
    }

    fun findMaskedArea(src: Mat) {

        // 1. 转换为灰度图
        val gray = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY)

        // 2. 二值化处理
        val binary = Mat()
        Imgproc.threshold(gray, binary, 100.0, 255.0, Imgproc.THRESH_BINARY_INV)

        // 3. 形态学操作
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
        Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_OPEN, kernel)

        // 4. 查找轮廓
        val contours: MutableList<MatOfPoint> = mutableListOf()
        val hierarchy = Mat()
        Imgproc.findContours(binary, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        // 5. 筛选出被遮盖的区域
        for (i in contours.indices) {
            val area = Imgproc.contourArea(contours[i])
            if (area > 1000) { // 设置一个最小面积阈值
                val rect: Rect = Imgproc.boundingRect(contours[i])
                // 绘制矩形框
                Imgproc.rectangle(src, rect.tl(), rect.br(), Scalar(255.0, 0.0, 0.0), 2)
            }
        }

        // 6. 显示或保存结果
        Imgcodecs.imwrite("output_masked_area.jpg", src)
    }


}