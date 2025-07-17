package com.example.myapplication.click

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.FileUtils
import com.nwq.baseutils.MaskUtils
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.ClickEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ClickDetailViewModel : ViewModel() {

    private val mClickDao = IdentifyDatabase.getDatabase().clickDao()
    private val mTargetRecordDao = IdentifyDatabase.getDatabase().findTargetRecordDao()
    public var mClickEntity: ClickEntity? = null

    var path: String? = null
    var storageType: Int = MatUtils.STORAGE_ASSET_TYPE
    var mSrcBitmap: Bitmap? = null //这个是原始的整个屏幕的图

    //找图区域
    var findArea: CoordinateArea? = null

    //进行生成时候选的区域
    var targetOriginalArea: CoordinateArea? = null

    var clickArea: CoordinateArea? = null
    public suspend fun init(id: Long) {
        withContext(Dispatchers.IO){
            mClickEntity = mClickDao.findByKeyId(id)
            mClickEntity?.let {
                updateFindTarget(it.findTargetId)
                clickArea = CoordinateArea(it.x, it.y, it.with, it.height, it.isRound);
            }
        }
    }

    public fun updateFindTarget(targetId: Long) {
        if (targetId <= 0)
            return
        mClickEntity?.findTargetId = targetId
        viewModelScope.launch(Dispatchers.IO) {
            mTargetRecordDao.findById(targetId)?.let { findTargetRecord ->
                path = findTargetRecord.path
                storageType = findTargetRecord.storageType ?: MatUtils.REAL_PATH_TYPE
                mSrcBitmap = FileUtils.getBitmapByType(path, storageType)
                targetOriginalArea = findTargetRecord.targetOriginalArea
                findArea = findTargetRecord.findArea
            }
        }
    }

    fun save() {
        viewModelScope.launch (Dispatchers.IO){
            clickArea?.let { clickArea->
                mClickEntity?.let {
                    it.x = clickArea.x
                    it.y = clickArea.y
                    it.with = clickArea.width
                    it.height = clickArea.height
                }
            }
            mClickEntity?.let {
                mClickDao.update(it)
            }
        }
    }


}
