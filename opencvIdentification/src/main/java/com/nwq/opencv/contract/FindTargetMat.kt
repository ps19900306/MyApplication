package com.nwq.opencv.contract

import ImageDescriptorEntity
import android.graphics.Bitmap
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.CommonCallBack
import com.nwq.baseutils.CommonCallBack2
import com.nwq.baseutils.Mat2ArrayUtils
import com.nwq.opencv.db.IdentifyDatabase
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfKeyPoint
import org.opencv.features2d.ORB
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
        private val bImageDescriptorDao by lazy {
            IdentifyDatabase.getDatabase().imageDescriptorDao()
        }
    }


    private val dbKeyTag = "$tag$DB_SUFFIX"
    private val matKeyTag = "$tag$DB_SUFFIX"
    private val descriptorMat by lazy {

    }


    override fun findTarget(any: Any): CoordinateArea? {
        if (any is Mat)
            return findTargetBitmap(any)
        return null
    }

    fun findTargetBitmap(mat: Mat): CoordinateArea? {
        return null
    }


    fun builderTargetMat() {
        if (saveDb) {
            val imageDescriptorEntity = bImageDescriptorDao.getDescriptor(dbKeyTag)
            val descriptorMat=  if (imageDescriptorEntity == null) {
                val bitmap = bitmapTake.callBack()
                val maskMat = maskTake.callBack(bitmap)
                val descriptor =getDescriptorMat(bitmap,maskMat)
                val imageDescriptorEntity = ImageDescriptorEntity(keyTag = dbKeyTag, descriptors = Mat2ArrayUtils.matToByteArray(descriptor), matType = descriptor.type(), matRows = descriptor.rows(), matCols = descriptor.cols())
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
        }else{
            val bitmap = bitmapTake.callBack()
            val maskMat = maskTake.callBack(bitmap)
            getDescriptorMat(bitmap,maskMat)
        }
    }


    // 根据传入的数据获取到描述
    fun getDescriptorMat(bitmap: Bitmap, mask: Mat): Mat {
        // 1. 将 Bitmap 转换为 Mat
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        // 2. 将图像转换为灰度图（很多特征提取算法要求灰度图像）
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY)

        // 3. 创建 ORB 特征点检测器和描述符提取器
        val orb = ORB.create()

        // 4. 用于保存关键点和描述符
        val keypoints = MatOfKeyPoint()
        val descriptors = Mat()

        // 5. 检测关键点并提取描述符
        orb.detectAndCompute(mat, mask, keypoints, descriptors)

        // 6. 返回提取的描述符
        return descriptors
    }
}