package com.nwq.opencv.contract

import ImageDescriptorEntity
import android.graphics.Bitmap
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.CommonCallBack
import com.nwq.baseutils.CommonCallBack2
import com.nwq.baseutils.Mat2ArrayUtils
import com.nwq.opencv.db.IdentifyDatabase
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
import org.opencv.features2d.Feature2D
import org.opencv.features2d.ORB
import org.opencv.features2d.SIFT
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

abstract class FindTargetMat(
    tag: String,
    val bitmapTake: CommonCallBack<Bitmap>,
    val maskTake: CommonCallBack2<Bitmap, Mat>,
    val saveDb: Boolean = true
) :
    FindTarget(tag) {

    companion object {
        const val MAT_SUFFIX = "_mat"
        const val DB_SUFFIX = "_db"

        // 创建ORB检测器 可以根据需求替换提取器
        val feature2D: Feature2D = ORB.create()

        private val bImageDescriptorDao by lazy {
            IdentifyDatabase.getDatabase().imageDescriptorDao()
        }
    }


    private val dbKeyTag = "$tag$DB_SUFFIX"
    private val matKeyTag = "$tag$DB_SUFFIX"
    private val descriptorMat by lazy {
        builderTargetMat()
    }


    override fun findTarget(any: Any): CoordinateArea? {
        if (any is Mat)
            return findTargetBitmap(any)
        return null
    }

    fun findTargetBitmap(mat: Mat): CoordinateArea? {
         return null

//        // 查找图A和图B的关键点和描述符
//        val keypointsA = MatOfKeyPoint()
//        val descriptorsA = Mat()
//        feature2D.detectAndCompute(mat, Mat(), keypointsA, descriptorsA)
//
//
//
//        // 使用BFMatcher进行特征点匹配
//        val bfMatcher = BFMatcher.create(Core.NORM_HAMMING, true)
//        val matches = MatOfDMatch()
//        bfMatcher.match(descriptorMat, descriptorsA, matches)
//
//        // 对匹配点进行过滤（选择最佳匹配）
//        val matchesList = matches.toList().sortedBy { it.distance }.take(50)
//
//        if (matchesList.isEmpty()) {
//            return null // 没有找到合适的匹配
//        }
//
//        // 获取匹配到的关键点的坐标
//        val srcPoints = MatOfPoint2f(*matchesList.map { keypointsB.toArray()[it.queryIdx].pt }.toTypedArray())
//        val dstPoints = MatOfPoint2f(*matchesList.map { keypointsA.toArray()[it.trainIdx].pt }.toTypedArray())
//
//        // 计算图B在图A中的透视变换矩阵
//        val homography = Calib3d.findHomography(srcPoints, dstPoints, Calib3d.RANSAC, 5.0)
//
//        if (homography.empty()) {
//            return null // 无法找到合适的变换
//        }
//
//        // 图像B的边界点
//        val points = arrayOf(
//            Point(0.0, 0.0),
//            Point(imageB.cols().toDouble(), 0.0),
//            Point(imageB.cols().toDouble(), imageB.rows().toDouble()),
//            Point(0.0, imageB.rows().toDouble())
//        )
//
//        val srcCorners = MatOfPoint2f(*points)
//        val dstCorners = MatOfPoint2f()
//
//        // 计算变换后的坐标
//        Core.perspectiveTransform(srcCorners, dstCorners, homography)
//        val resultPoints = dstCorners.toArray()
//
//        // 计算边界框
//        val x = resultPoints.minOf { it.x }.toInt()
//        val y = resultPoints.minOf { it.y }.toInt()
//        val w = (resultPoints.maxOf { it.x } - x).toInt()
//        val h = (resultPoints.maxOf { it.y } - y).toInt()
//
//        return CoordinateArea(x, y, w, h)
    }


    private fun builderTargetMat(): Mat {
        return if (saveDb) {
            val imageDescriptorEntity = bImageDescriptorDao.getDescriptor(dbKeyTag)
            if (imageDescriptorEntity == null) {
                val bitmap = bitmapTake.callBack()
                val maskMat = maskTake.callBack(bitmap)
                val descriptor = getDescriptorMat(bitmap, maskMat)
                val imageDescriptorEntity = ImageDescriptorEntity(
                    keyTag = dbKeyTag,
                    descriptors = Mat2ArrayUtils.matToByteArray(descriptor),
                    matType = descriptor.type(),
                    matRows = descriptor.rows(),
                    matCols = descriptor.cols()
                )
                bImageDescriptorDao.insertDescriptor(imageDescriptorEntity)
                descriptor
            } else {
                Mat2ArrayUtils.byteArrayToMat(
                    imageDescriptorEntity.descriptors,
                    imageDescriptorEntity.matType,
                    imageDescriptorEntity.matRows,
                    imageDescriptorEntity.matCols
                )
            }
        } else {
            val bitmap = bitmapTake.callBack()
            val maskMat = maskTake.callBack(bitmap)
            getDescriptorMat(bitmap, maskMat)
        }
    }


    // 根据传入的数据获取到描述
    private fun getDescriptorMat(bitmap: Bitmap, mask: Mat): Mat {
        // 1. 将 Bitmap 转换为 Mat
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        // 2. 将图像转换为灰度图（很多特征提取算法要求灰度图像）
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY)


        // 4. 用于保存关键点和描述符
        val keypoints = MatOfKeyPoint()
        val descriptors = Mat()

        // 5. 检测关键点并提取描述符
        feature2D.detectAndCompute(mat, mask, keypoints, descriptors)

        val keypointList = keypoints.toArray()
        for (keypoint in keypointList) {
            val x = keypoint.pt.x  // 关键点的 x 坐标
            val y = keypoint.pt.y  // 关键点的 y 坐标
            val size = keypoint.size  // 关键点的尺寸
            val angle = keypoint.angle  // 关键点的方向
            val response = keypoint.response  // 关键点的响应值
            val octave = keypoint.octave  // 关键点的金字塔层次
            val classId = keypoint.class_id  // 关键点的分类ID

            // 你可以在这里处理每个关键点的信息
            println("KeyPoint: x=$x, y=$y, size=$size, angle=$angle, response=$response, octave=$octave, classId=$classId")
        }


        // 6. 返回提取的描述符
        return descriptors
    }



}