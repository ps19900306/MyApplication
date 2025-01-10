package com.example.myapplication.verify_results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nwq.adapter.CheckKeyText
import com.nwq.adapter.IKeyText
import com.nwq.adapter.KeyTextImp
import com.nwq.opencv.FindTargetType
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.TargetVerifyResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class VerifyResultPViewModel(val tag: String) : ViewModel() {

    public val selectedType = MutableStateFlow<Int>(FindTargetType.RGB)
    public val isDo = MutableStateFlow<Boolean>(false)
    public val isEffective = MutableStateFlow<Boolean>(false)
    public val isPass = MutableStateFlow<Boolean>(false)
    private val mTargetRgbDao = IdentifyDatabase.getDatabase().findTargetRgbDao()
    private val mTargetHsvDao = IdentifyDatabase.getDatabase().findTargetHsvDao()
    private val mTargetImgDao = IdentifyDatabase.getDatabase().findTargetImgDao()
    private val mTargetMatDao = IdentifyDatabase.getDatabase().findTargetMatDao()

    public val resultFlow by lazy {
        combine(selectedType, isPass, isEffective, isDo) { type, isPass, isEffective, isDo ->
            IdentifyDatabase.getDatabase().targetVerifyResultDao()
                .findByTagTypeIsPassIsEffectiveIsDo(tag, type, isPass, isEffective, isDo)
        }.flowOn(Dispatchers.IO)
    }

    public val typeList by lazy {
        val list = mutableListOf<CheckKeyText>();
        if (mTargetRgbDao.findByKeyTag(tag) != null) {
            list.add(CheckKeyText(FindTargetType.RGB, "Rgb"))
        }
        if (mTargetHsvDao.findByKeyTag(tag) != null) {
            list.add(CheckKeyText(FindTargetType.HSV, "Hsv"))
        }
        if (mTargetImgDao.findByKeyTag(tag) != null) {
            list.add(CheckKeyText(FindTargetType.IMG, "Img"))
        }
        if (mTargetMatDao.findByKeyTag(tag) != null) {
            list.add(CheckKeyText(FindTargetType.MAT, "Mat"))
        }
        list.getOrNull(0)?.let {
            it.isChecked = true
            selectedType.value = it.getKey()
        }
        list
    }


    fun setType(type: Int) {
        selectedType.value = type
    }


    fun setIsPass(isPass: Boolean) {
        this.isPass.value = isPass
    }

    fun setIsEffective(isEffective: Boolean) {
        this.isEffective.value = isEffective
    }

    fun setIsDo(isDo: Boolean) {
        this.isDo.value = isDo
    }

    fun initData(id: Long): TargetVerifyResult? {
        return IdentifyDatabase.getDatabase().targetVerifyResultDao().findById(id)
    }

    fun setIsEffectiveForId(id: Long, isEffective: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            IdentifyDatabase.getDatabase().targetVerifyResultDao().findById(id)?.let {
                it.isEffective = isEffective
                IdentifyDatabase.getDatabase().targetVerifyResultDao().update(it)
            }
        }
    }

    fun setIsPassDo(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            IdentifyDatabase.getDatabase().targetVerifyResultDao().findById(id)?.let {
                it.isDo = true
                IdentifyDatabase.getDatabase().targetVerifyResultDao().update(it)
            }
        }
    }


}
