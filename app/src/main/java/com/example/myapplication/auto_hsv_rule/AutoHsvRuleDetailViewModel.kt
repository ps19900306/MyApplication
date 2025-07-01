package com.example.myapplication.auto_hsv_rule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.AutoRulePointEntity
import com.nwq.opencv.hsv.HSVRule
import com.nwq.simplelist.ICheckText
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.descriptors.PrimitiveKind

class AutoHsvRuleDetailViewModel : ViewModel() {

    private val mAutoRulePointDao = IdentifyDatabase.getDatabase().autoRulePointDao()
    private var mAutoRulePointEntity: AutoRulePointEntity? = null
    public var prList: MutableStateFlow<List<ICheckText<HSVRule>>> = MutableStateFlow(
        listOf()
    )


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
                prList.tryEmit(list)
            }
        }
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
                mAutoRulePointDao.update(entity)
            }
        }
    }
}