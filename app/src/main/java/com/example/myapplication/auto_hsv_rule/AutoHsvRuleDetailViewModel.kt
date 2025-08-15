package com.example.myapplication.auto_hsv_rule

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nwq.adapter.KeyTextImp

import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.FileUtils
import com.nwq.baseutils.MatUtils
import com.nwq.loguitls.L
import com.nwq.opencv.AutoHsvRuleType
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.AutoRulePointEntity
import com.nwq.opencv.hsv.HSVRule
import com.nwq.simplelist.ICheckText
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AutoHsvRuleDetailViewModel : ViewModel() {

    private val mAutoRulePointDao = IdentifyDatabase.getDatabase().autoRulePointDao()
    private var mAutoRulePointEntity: AutoRulePointEntity? = null
    public var prList: MutableStateFlow<List<ICheckText<HSVRule>>> = MutableStateFlow(
        listOf()
    )

    //进行生成时候选的区域
    public var targetOriginalArea: CoordinateArea? = null
    public var path: String? = null
    public var storageType: Int = MatUtils.STORAGE_ASSET_TYPE
    public var mSrcBitmap: Bitmap? = null  //原图
    public var mSelectBitmap: Bitmap? = null //选择区域
    public var typeSelectP = 0;
    private var type = AutoHsvRuleType.KEY_POINT
    public val typeList by lazy {
        listOf(
            KeyTextImp("过滤生成蒙版Mask", AutoHsvRuleType.FILTER_MASK),
            KeyTextImp("过滤去除颜色成蒙版Mask", AutoHsvRuleType.RE_FILTER_MASK),
            KeyTextImp("选取关键点", AutoHsvRuleType.KEY_POINT),
            KeyTextImp("过滤生成关键点再去除对应颜色", AutoHsvRuleType.RE_KEY_POINT),
            KeyTextImp("通过颜色块 然后获取边框获取关键点", AutoHsvRuleType.BE_FRAME_POINT)
        )
    }

    public fun setOnTypeSelectP(typeP: Int) {
        L.d("AutoHsvRuleDetailViewModel", "setOnTypeSelectP:", "2025/7/18/16:42")
        typeSelectP = typeP
        type = typeList[typeP].getKey()
    }

    public fun init(id: Long) {
        if (mAutoRulePointEntity != null)
            return
        viewModelScope.launch(Dispatchers.IO) {
            mAutoRulePointEntity = mAutoRulePointDao.findByKeyId(id)
            mAutoRulePointEntity?.let { entity ->
                val list = entity.prList.map { data ->
                    ICheckTextWrap<HSVRule>(data) {
                        it.toString()
                    }
                }
                targetOriginalArea = entity.targetOriginalArea
                path = entity.path
                storageType = entity.storageType
                mSrcBitmap = FileUtils.getBitmapByType(path, storageType)
                prList.tryEmit(list)
                type = entity.type

                typeList.forEachIndexed { index, keyTextImp ->
                    if (entity.type == keyTextImp.getKey()) {
                        typeSelectP = index
                    }
                }
            }
        }
    }


    public fun getSelectBitmap(): Bitmap? {
        if (mSelectBitmap != null)
            return mSelectBitmap
        if (mSrcBitmap != null) {
            if (targetOriginalArea != null) {
                //根据区域队Bitmap进行裁剪
                mSelectBitmap = Bitmap.createBitmap(
                    mSrcBitmap!!,
                    targetOriginalArea!!.x,
                    targetOriginalArea!!.y,
                    targetOriginalArea!!.width,
                    targetOriginalArea!!.height
                )
            }else{
                return mSrcBitmap
            }
        }
        return mSelectBitmap
    }

    public fun addData(item: ICheckText<HSVRule>) {
        val list = mutableListOf<ICheckText<HSVRule>>()
        list.addAll(prList.value)
        list.add(item)
        prList.tryEmit(list)
    }

    public fun upData(list: List<ICheckText<HSVRule>>) {
        prList.tryEmit(list)
    }

    public fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            mAutoRulePointEntity?.let { entity ->
                entity.prList = prList.value.map { it.getT() }
                entity.storageType = storageType
                entity.path = path
                entity.targetOriginalArea = targetOriginalArea
                entity.type = type
                mAutoRulePointDao.update(entity)
            }
        }
    }
}