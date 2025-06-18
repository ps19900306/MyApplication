package com.example.myapplication.find_target

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.ICoordinate
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.FindTargetHsvEntity
import com.nwq.opencv.db.entity.FindTargetImgEntity
import com.nwq.opencv.db.entity.FindTargetMatEntity
import com.nwq.opencv.db.entity.FindTargetRecord
import com.nwq.opencv.db.entity.FindTargetRgbEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [com.nwq.opencv.db.entity.FindTargetRecord]
 * [com.nwq.opencv.db.entity.FindTargetHsvEntity]
 * [com.nwq.opencv.db.entity.FindTargetRgbEntity]
 * [com.nwq.opencv.db.entity.FindTargetImgEntity]
 * [com.nwq.opencv.db.entity.FindTargetMatEntity]
 */
class FindTargetDetailModel : ViewModel() {


    var mFindTargetRecord: FindTargetRecord? = null
    var mFindTargetHsvEntity: FindTargetHsvEntity? = null
    var mFindTargetRgbEntity: FindTargetRgbEntity? = null
    var mFindTargetImgEntity: FindTargetImgEntity? = null
    var mFindTargetMatEntity: FindTargetMatEntity? = null

    //所有的必须一致  如果需要重新生成需要清除掉原有数据
    //进行生成时候选的区域
    var targetOriginalArea: CoordinateArea? = null

    //这个是找图范围
    var findArea: CoordinateArea? = null

    var path: String? = null
    var storageType: Int = MatUtils.STORAGE_ASSET_TYPE

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


}