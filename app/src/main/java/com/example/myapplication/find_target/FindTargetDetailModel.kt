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
import com.nwq.loguitls.L
import org.opencv.core.Mat

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