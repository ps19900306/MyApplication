package com.nwq.opencv.find_target

import com.nwq.opencv.db.entity.ImageDescriptorEntity
import android.graphics.Bitmap
import com.nwq.baseobj.CoordinateArea
import com.nwq.callback.CommonCallBack
import com.nwq.callback.CommonCallBack2
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.db.IdentifyDatabase
import org.opencv.android.Utils
import org.opencv.calib3d.Calib3d
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.features2d.BFMatcher
import org.opencv.features2d.Feature2D
import org.opencv.features2d.SIFT
import org.opencv.imgproc.Imgproc


//进行特征点匹配
abstract class FindTargetMat(
    tag: String,
    srcArea: CoordinateArea,
    val bitmapTake: CommonCallBack<Bitmap>,//这个是
    val maskTake: CommonCallBack2<Bitmap, Mat>,
    val saveDb: Boolean = true,
    val finArea: CoordinateArea?=null,

) : FindTarget(tag,srcArea) {

    companion object {
        const val MAT_SUFFIX = "_mat"
        const val DB_SUFFIX = "_db"

        // 创建SIFT检测器 可以根据需求替换提取器
        val feature2D: Feature2D = SIFT.create()

        private val bImageDescriptorDao by lazy {
            IdentifyDatabase.getDatabase().imageDescriptorDao()
        }
    }


    private val dbKeyTag = "$tag$DB_SUFFIX"
    private val matKeyTag = "$tag$DB_SUFFIX"
    private val descriptorMat by lazy {
        builderTargetMat()
    }

    //builderTargetMat 会初始化这下面个值
    private lateinit var points: Array<Point> //这个是原图像的大小
    private lateinit var mKeypoints: MatOfKeyPoint//这个是特征点的


    override suspend fun findTarget(): CoordinateArea? {
        val srcMat = imgTake.getHsvMat(finArea) ?: return null
        return findTargetBitmap(srcMat)
    }



    fun findTargetBitmap(mat: Mat): CoordinateArea? {

        // 查找图A和图B的关键点和描述符
        val keypointsA = MatOfKeyPoint()
        val descriptorsA = Mat()
        feature2D.detectAndCompute(mat, Mat(), keypointsA, descriptorsA)


        // 使用BFMatcher进行特征点匹配
        val bfMatcher = BFMatcher.create(Core.NORM_HAMMING, true)
        val matches = MatOfDMatch()
        bfMatcher.match(descriptorMat, descriptorsA, matches)

        // 对匹配点进行过滤（选择最佳匹配）
        val matchesList = matches.toList().sortedBy { it.distance }.take(50)

        if (matchesList.isEmpty()) {
            return null // 没有找到合适的匹配
        }

        // 获取匹配到的关键点的坐标
        val srcPoints =
            MatOfPoint2f(*matchesList.map { mKeypoints.toArray()[it.queryIdx].pt }.toTypedArray())
        val dstPoints =
            MatOfPoint2f(*matchesList.map { keypointsA.toArray()[it.trainIdx].pt }.toTypedArray())

        // 计算图B在图A中的透视变换矩阵
        val homography = Calib3d.findHomography(srcPoints, dstPoints, Calib3d.RANSAC, 5.0)

        if (homography.empty()) {
            return null // 无法找到合适的变换
        }

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

        return CoordinateArea(x, y, w, h)
    }


    private fun builderTargetMat(): Mat {
        return if (saveDb) {
            val imageDescriptorEntity = bImageDescriptorDao.getDescriptor(dbKeyTag)
            if (imageDescriptorEntity == null) {
                val bitmap = bitmapTake.callBack()
                val maskMat = maskTake.callBack(bitmap)
                val descriptor = buildImageDescriptorEntity(bitmap, maskMat, true)
                descriptor
            } else {
                mKeypoints = imageDescriptorEntity.getMatOfKeyPoint();
                points = imageDescriptorEntity.pointList.toTypedArray()
                MatUtils.byteArrayToMat(
                    imageDescriptorEntity.descriptors,
                    imageDescriptorEntity.matType,
                    imageDescriptorEntity.matRows,
                    imageDescriptorEntity.matCols
                )
            }
        } else {
            val bitmap = bitmapTake.callBack()
            val maskMat = maskTake.callBack(bitmap)
            buildImageDescriptorEntity(bitmap, maskMat, false)
        }
    }


    // 根据传入的数据获取到描述
    private fun buildImageDescriptorEntity(bitmap: Bitmap, mask: Mat, saveDb: Boolean): Mat {
        // 1. 将 Bitmap 转换为 Mat
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        points = arrayOf(
            Point(0.0, 0.0),
            Point(mat.cols().toDouble(), 0.0),
            Point(mat.cols().toDouble(), mat.rows().toDouble()),
            Point(0.0, mat.rows().toDouble())
        )

        // 2. 将图像转换为灰度图（很多特征提取算法要求灰度图像）
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY)

        // 4. 用于保存关键点和描述符
        val keypoints = MatOfKeyPoint()
        val descriptors = Mat()

        // 5. 检测关键点并提取描述符
        feature2D.detectAndCompute(mat, mask, keypoints, descriptors)
        mKeypoints = keypoints

        if (saveDb) {
            val keypointList = keypoints.toArray().toList()
            val imageDescriptorEntity = ImageDescriptorEntity(
                keyTag = dbKeyTag,
                descriptors = MatUtils.matToByteArray(descriptors),
                matType = descriptors.type(),
                matRows = descriptors.rows(),
                matCols = descriptors.cols(),
                keyPointList = keypointList,
                pointList = points.toList()
            )
            bImageDescriptorDao.insertDescriptor(imageDescriptorEntity)
        }
        return descriptors
    }


}