package com.example.myapplication.find_target

import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.ICoordinate
import com.nwq.baseutils.FileUtils
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.T
import com.nwq.opencv.IAutoRulePoint
import com.nwq.opencv.IFindTarget
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.FindTargetHsvEntity
import com.nwq.opencv.db.entity.FindTargetImgEntity
import com.nwq.opencv.db.entity.FindTargetMatEntity
import com.nwq.opencv.db.entity.FindTargetRecord
import com.nwq.opencv.db.entity.FindTargetRgbEntity
import com.nwq.opencv.hsv.PointHSVRule
import com.nwq.opencv.rgb.PointRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.core.Point
import androidx.core.graphics.get
import com.nwq.baseutils.MaskUtils
import com.nwq.loguitls.L
import com.nwq.opencv.db.entity.ImageDescriptorEntity
import org.opencv.core.Mat
import org.opencv.core.MatOfKeyPoint
import org.opencv.features2d.Feature2D
import org.opencv.features2d.SIFT
import org.opencv.imgproc.Imgproc

/**
 * [com.nwq.opencv.db.entity.FindTargetRecord]
 * [com.nwq.opencv.db.entity.FindTargetHsvEntity]
 * [com.nwq.opencv.db.entity.FindTargetRgbEntity]
 * [com.nwq.opencv.db.entity.FindTargetImgEntity]
 * [com.nwq.opencv.db.entity.FindTargetMatEntity]
 */
class FindTargetDetailModel : ViewModel() {

    private val TAG = "FindTargetDetailModel"
    var mFindTargetRecord: FindTargetRecord? = null
    var mFindTargetHsvEntity: FindTargetHsvEntity? = null
    var mFindTargetRgbEntity: FindTargetRgbEntity? = null
    var mFindTargetImgEntity: FindTargetImgEntity? = null
    var mFindTargetMatEntity: FindTargetMatEntity? = null

    //这个是找图范围
    var findArea: CoordinateArea? = null
    private var maskType: Int = MaskUtils.UN_SET_MASK

    //所有的必须一致  如果需要重新生成需要清除掉原有数据
    //进行生成时候选的区域
    var targetOriginalArea: CoordinateArea? = null
    var autoRulePoint: IAutoRulePoint? = null
    var path: String? = null
    var storageType: Int = MatUtils.STORAGE_ASSET_TYPE
    var mBitmap: Bitmap? = null


    private val mTargetRecordDao = IdentifyDatabase.getDatabase().findTargetRecordDao()
    private val mTargetRgbDao = IdentifyDatabase.getDatabase().findTargetRgbDao()
    private val mTargetHsvDao = IdentifyDatabase.getDatabase().findTargetHsvDao()
    private val mTargetImgDao = IdentifyDatabase.getDatabase().findTargetImgDao()
    private val mTargetMatDao = IdentifyDatabase.getDatabase().findTargetMatDao()


    public suspend fun init(targetId: Long) {
        return withContext(Dispatchers.IO) {
            mFindTargetRecord = mTargetRecordDao.findById(targetId)
            path = mFindTargetRecord?.path
            storageType = mFindTargetRecord?.storageType ?: MatUtils.REAL_PATH_TYPE
            mFindTargetHsvEntity = mTargetHsvDao.findByKeyTag(mFindTargetRecord?.keyTag ?: "")
            mFindTargetRgbEntity = mTargetRgbDao.findByKeyTag(mFindTargetRecord?.keyTag ?: "")
            mFindTargetImgEntity = mTargetImgDao.findByKeyTag(mFindTargetRecord?.keyTag ?: "")
            mFindTargetMatEntity = mTargetMatDao.findByKeyTag(mFindTargetRecord?.keyTag ?: "")
            mBitmap = FileUtils.getBitmapByType(path, storageType)
        }
    }

    public fun clear() {
        targetOriginalArea = null
        findArea = null
        path = null
        viewModelScope.launch(Dispatchers.IO) {
            mFindTargetHsvEntity?.let { mTargetHsvDao.delete(it) }
            mFindTargetRgbEntity?.let { mTargetRgbDao.delete(it) }
            mFindTargetImgEntity?.let { mTargetImgDao.delete(it) }
            mFindTargetMatEntity?.let { mTargetMatDao.delete(it) }

        }
    }

    fun save(path: String?, type: Int, originalArea: ICoordinate?, findArea: ICoordinate?) {
        this.path = path
        this.storageType = type
        originalArea?.let { targetOriginalArea = it as CoordinateArea }
        findArea?.let { this.findArea = it as CoordinateArea }
        mFindTargetRecord?.path = path ?: "";
        mFindTargetRecord?.storageType = type
        viewModelScope.launch(Dispatchers.IO) {
            mTargetRecordDao.update(mFindTargetRecord!!)
        }

    }

    fun updateHsvRule(it: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            autoRulePoint = IdentifyDatabase.getDatabase().autoRulePointDao().findByKeyId(it)
        }
    }


    fun performAutoFindRule(hsv: Boolean, rgb: Boolean) {
        viewModelScope.launch {
            val sMat = MatUtils.bitmapToMat(mBitmap!!, targetOriginalArea)
            val keyPointList = autoRulePoint!!.autoPoint(sMat)
            if (hsv) {
                buildRgbFindTarget(mBitmap!!, targetOriginalArea!!, keyPointList)
            }
            if (rgb) {
                buildHsvFindTarget(sMat, targetOriginalArea!!, keyPointList)
            }
            T.show("构建结束")
        }
    }

    private fun buildRgbFindTarget(
        bitmap: Bitmap,
        selectArea: CoordinateArea,
        pointList: MutableList<Point>
    ) {
        val list = mutableListOf<PointRule>()
        pointList.forEach {
            val rgbInt = bitmap[(it.x + selectArea.x).toInt(), (it.y + selectArea.y).toInt()]
            val pointRule = PointRule(
                (it.x + selectArea.x).toInt(),
                (it.y + selectArea.y).toInt(),
                rgbInt.red,
                rgbInt.green,
                rgbInt.blue
            )
            list.add(pointRule)
        }
        val data = FindTargetRgbEntity(
            keyTag = mFindTargetRecord?.keyTag ?: "",
            targetOriginalArea = selectArea,
            findArea = findArea,
            prList = list,
        )
        IdentifyDatabase.getDatabase().findTargetRgbDao().insert(data)
        L.d(TAG, "buildRgbFindTarget")
    }

    private fun buildHsvFindTarget(
        selectMat: Mat,
        selectArea: CoordinateArea, pointList: MutableList<Point>
    ) {
        val list = mutableListOf<PointHSVRule>()
        pointList.forEach {
            val hsvArray = selectMat.get(it.y.toInt(), it.x.toInt())
            val pointRule = PointHSVRule(
                (it.x + selectArea.x).toInt(),
                (it.y + selectArea.y).toInt(),
                hsvArray[0].toInt(),
                hsvArray[1].toInt(),
                hsvArray[2].toInt()
            )
            list.add(pointRule)
        }
        val data = FindTargetHsvEntity(
            keyTag = mFindTargetRecord?.keyTag ?: "",
            targetOriginalArea = selectArea,
            findArea = findArea,
            prList = list
        )
        IdentifyDatabase.getDatabase().findTargetHsvDao().insert(data)
        L.d(TAG, "buildRgbFindTarget")
    }


    private fun buildImgFindTarget(selectMat: Mat, selectArea: CoordinateArea) {
        //保存图片
        val bitmap = MatUtils.hsvMatToBitmap(selectMat)
        FileUtils.saveBitmapToExternalStorageImg(bitmap, mFindTargetRecord?.keyTag ?: "")

        val data = FindTargetImgEntity(
            keyTag = mFindTargetRecord?.keyTag ?: "",
            targetOriginalArea = selectArea,
            findArea = findArea,
            storageType = MatUtils.STORAGE_EXTERNAL_TYPE,
            maskType = maskType
        )
        IdentifyDatabase.getDatabase().findTargetImgDao().insert(data)
    }

    private fun buildMatFindTarget(selectMat: Mat, selectArea: CoordinateArea) {
        val bitmap = MatUtils.hsvMatToBitmap(selectMat!!)
        FileUtils.saveBitmapToExternalStorageImg(bitmap, mFindTargetRecord?.keyTag ?: "")

        //将特征点保存到数据库
        buildImageDescriptorEntity(selectMat!!, MaskUtils.getMaskMat(selectMat, maskType))
        val data = FindTargetMatEntity(
            keyTag = mFindTargetRecord?.keyTag ?: "",
            targetOriginalArea = selectArea!!,
            findArea = findArea,
            storageType = MatUtils.STORAGE_EXTERNAL_TYPE,
            maskType = maskType
        )
        IdentifyDatabase.getDatabase().findTargetMatDao().insert(data)
    }


    // 根据传入的数据获取到描述
    private fun buildImageDescriptorEntity(hsvMat: Mat, mask: Mat?) {
        val points = arrayOf(
            Point(0.0, 0.0),
            Point(hsvMat.cols().toDouble(), 0.0),
            Point(hsvMat.cols().toDouble(), hsvMat.rows().toDouble()),
            Point(0.0, hsvMat.rows().toDouble())
        )

        // 2. 将图像转换为灰度图（很多特征提取算法要求灰度图像）
        // 创建一个用于存储 BGR 图像的 Mat 对象
        val bgrMat = Mat()
        // 创建一个用于存储灰度图的 Mat 对象
        val grayMat = Mat()
        // 先将 HSV 图像转换为 BGR 图像
        Imgproc.cvtColor(hsvMat, bgrMat, Imgproc.COLOR_HSV2BGR)
        // 再将 BGR 图像转换为灰度图
        Imgproc.cvtColor(bgrMat, grayMat, Imgproc.COLOR_BGR2GRAY)


        // 4. 用于保存关键点和描述符
        val keypoints = MatOfKeyPoint()
        val descriptors = Mat()

        val feature2D: Feature2D = SIFT.create()
        // 5. 检测关键点并提取描述符
        feature2D.detectAndCompute(grayMat, mask, keypoints, descriptors)

        val keypointList = keypoints.toArray().toList()
        val imageDescriptorEntity = ImageDescriptorEntity(
            keyTag = mFindTargetRecord?.keyTag ?: "",
            descriptors = MatUtils.matToByteArray(descriptors),
            matType = descriptors.type(),
            matRows = descriptors.rows(),
            matCols = descriptors.cols(),
            keyPointList = keypointList,
            pointList = points.toList()
        )
        IdentifyDatabase.getDatabase().imageDescriptorDao().insertDescriptor(imageDescriptorEntity)
    }


    fun saveHsvTarget() {
        mFindTargetHsvEntity?.let { entity ->
            viewModelScope.launch(Dispatchers.IO) {
                mTargetHsvDao.insert(entity)
            }
        }
    }

    fun saveRbgTarget() {
        mFindTargetRgbEntity?.let { entity ->
            viewModelScope.launch(Dispatchers.IO) {
                mTargetRgbDao.insert(entity)
            }
        }
    }

}