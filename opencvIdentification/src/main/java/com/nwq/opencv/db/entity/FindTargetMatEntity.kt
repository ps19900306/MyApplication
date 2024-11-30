package com.nwq.opencv.db.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.CoordinateUtils
import com.nwq.baseutils.MaskUtils
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.IFindTarget
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.converters.CoordinateAreaConverters
import com.nwq.opencv.db.converters.PointHSVRuleConverters
import com.nwq.opencv.hsv.PointHSVRule
import org.opencv.android.Utils
import org.opencv.calib3d.Calib3d
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.features2d.BFMatcher
import org.opencv.features2d.Feature2D
import org.opencv.features2d.SIFT
import org.opencv.imgproc.Imgproc


@Entity(tableName = "find_target_mat")
data class FindTargetMatEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    //识别标签 比如"主菜单","位置菜单"
    val keyTag: String,

    //进行生成时候选的区域
    val targetOriginalArea: CoordinateArea,

    //这个是找图范围
    var findArea: CoordinateArea? = null,

    //这个文件存放的类型现在考虑放外部存储或者asset文件夹 测试时候考虑为外部 打包时候考虑放asset
    val storageType: Int = MatUtils.STORAGE_ASSET_TYPE,

    //生成匹配蒙版的类型
    val maskType: Int = 0,

    ) : IFindTarget {


    companion object {

        // 创建SIFT检测器 可以根据需求替换提取器
        val feature2D: Feature2D = SIFT.create()

        private val bImageDescriptorDao by lazy {
            IdentifyDatabase.getDatabase().imageDescriptorDao()
        }
    }


    // 目标图片的 Mat 对象
    private val targetMat by lazy {
        val templateMat = MatUtils.readHsvMat(storageType, keyTag)
        templateMat
    }

    //mask图标
    private val maskMat by lazy {
        MaskUtils.getMaskMat(targetMat, maskType)
    }

    //描述信息
    private val descriptorMat by lazy {
        builderTargetMat()
    }

    @Ignore
    private lateinit var points: Array<Point> //这个是原图像的大小 用来还原大小

    @Ignore
    private lateinit var mKeypoints: MatOfKeyPoint//这个是特征点的 用来


    override suspend fun findTarget(): CoordinateArea? {
        val srcMat = imgTake.getHsvMat(findArea) ?: return null
        return findTargetBitmap(srcMat)
    }


    override fun release() {

    }


    private fun findTargetBitmap(srcMat: Mat): CoordinateArea? {
        // 如果描述信息为空，则返回 null
        descriptorMat ?: return null

        // 查找图A和图B的关键点和描述符
        val keypointSrc = MatOfKeyPoint()
        val descriptorsSrc = Mat()

        feature2D.detectAndCompute(srcMat, Mat(), keypointSrc, descriptorsSrc)


        // 使用BFMatcher进行特征点匹配
        val bfMatcher = BFMatcher.create(Core.NORM_HAMMING, true)
        val matches = MatOfDMatch()
        bfMatcher.match(descriptorMat, descriptorsSrc, matches)

        // 对匹配点进行过滤（选择最佳匹配）
        val matchesList = matches.toList().sortedBy { it.distance }.take(50)

        if (matchesList.isEmpty()) {
            return null // 没有找到合适的匹配
        }

        // 获取匹配到的关键点的坐标
        val srcPoints =
            MatOfPoint2f(*matchesList.map { mKeypoints.toArray()[it.queryIdx].pt }.toTypedArray())
        val dstPoints =
            MatOfPoint2f(*matchesList.map { keypointSrc.toArray()[it.trainIdx].pt }.toTypedArray())

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
        val x = resultPoints.minOf { it.x }.toInt() + (findArea?.x ?: 0)
        val y = resultPoints.minOf { it.y }.toInt() + (findArea?.y ?: 0)
        val w = (resultPoints.maxOf { it.x } - x).toInt()
        val h = (resultPoints.maxOf { it.y } - y).toInt()

        return CoordinateArea(x, y, w, h)
    }


    private fun builderTargetMat(): Mat? {
        val imageDescriptorEntity = bImageDescriptorDao.getDescriptor(keyTag)
        return if (imageDescriptorEntity == null) {
            val descriptor = buildImageDescriptorEntity(targetMat!!, maskMat!!)
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
    }


    // 根据传入的数据获取到描述
    private fun buildImageDescriptorEntity(mat: Mat, mask: Mat): Mat {
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

//        if (saveDb) {
//            val keypointList = keypoints.toArray().toList()
//            val imageDescriptorEntity = ImageDescriptorEntity(
//                keyTag = keyTag,
//                descriptors = MatUtils.matToByteArray(descriptors),
//                matType = descriptors.type(),
//                matRows = descriptors.rows(),
//                matCols = descriptors.cols(),
//                keyPointList = keypointList,
//                pointList = points.toList()
//            )
//            bImageDescriptorDao.insertDescriptor(imageDescriptorEntity)
//        }
        return descriptors
    }


}

