package com.example.myapplication.verify_results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nwq.opencv.FindTargetType
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.TargetVerifyResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class VerifyResultPViewModel : ViewModel() {

    private val selectedType = MutableStateFlow<Int>(FindTargetType.RGB)
    private val isDo = MutableStateFlow<Boolean>(false)
    private val isEffective = MutableStateFlow<Boolean>(false)
    private val isPass = MutableStateFlow<Boolean>(false)
    private val tag = MutableStateFlow<String>("")

    private val resultFlow by lazy {
        combine(selectedType, tag, isPass, isEffective, isDo) { type, tag, isPass, isEffective, isDo ->
            IdentifyDatabase.getDatabase().targetVerifyResultDao().findByTagTypeIsPassIsEffectiveIsDo(tag, type, isPass, isEffective, isDo)
        }.flowOn(Dispatchers.IO)
    }


    fun setType(type: String?) {
        type?.toIntOrNull()?.let {
            selectedType.value = it
        }
    }

    fun setTag(tag: String) {
        this.tag.value = tag
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
