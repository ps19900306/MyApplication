package com.example.myapplication.find_target

import androidx.lifecycle.ViewModel
import com.nwq.baseobj.CoordinateArea
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.FindTargetHsvEntity
import com.nwq.opencv.db.entity.FindTargetImgEntity
import com.nwq.opencv.db.entity.FindTargetMatEntity
import com.nwq.opencv.db.entity.FindTargetRecord
import com.nwq.opencv.db.entity.FindTargetRgbEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * [com.nwq.opencv.db.entity.FindTargetRecord]
 * [com.nwq.opencv.db.entity.FindTargetHsvEntity]
 * [com.nwq.opencv.db.entity.FindTargetRgbEntity]
 * [com.nwq.opencv.db.entity.FindTargetImgEntity]
 * [com.nwq.opencv.db.entity.FindTargetMatEntity]
 */
class FindTargetDetailModel : ViewModel() {

    //进行生成时候选的区域
    var targetOriginalArea: CoordinateArea? = null
    var mFindTargetRecord: FindTargetRecord? = null
    var mFindTargetHsvEntity: FindTargetHsvEntity? = null
    var mFindTargetRgbEntity: FindTargetRgbEntity? = null
    var mFindTargetImgEntity: FindTargetImgEntity? = null
    var mFindTargetMatEntity: FindTargetMatEntity? = null

    //这个是找图范围
    var findArea: CoordinateArea? = null

    private val mTargetRecordDao = IdentifyDatabase.getDatabase().findTargetRecordDao()

    private val mTargetRgbDao = IdentifyDatabase.getDatabase().findTargetRgbDao()
    private val mTargetHsvDao = IdentifyDatabase.getDatabase().findTargetHsvDao()
    private val mTargetImgDao = IdentifyDatabase.getDatabase().findTargetImgDao()
    private val mTargetMatDao = IdentifyDatabase.getDatabase().findTargetMatDao()


    public suspend fun init(targetId: Long) {
        return withContext(Dispatchers.IO) {
            mFindTargetRecord = mTargetRecordDao.findById(targetId)
            mFindTargetHsvEntity = mTargetHsvDao.findByKeyTag(mFindTargetRecord?.keyTag ?: "")
            mFindTargetRgbEntity = mTargetRgbDao.findByKeyTag(mFindTargetRecord?.keyTag ?: "")
            mFindTargetImgEntity = mTargetImgDao.findByKeyTag(mFindTargetRecord?.keyTag ?: "")
            mFindTargetMatEntity = mTargetMatDao.findByKeyTag(mFindTargetRecord?.keyTag ?: "")
        }
    }


}