package com.example.myapplication.verify_results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.verify_results.data.VerifyResultAllSummarize
import com.example.myapplication.verify_results.data.VerifyResultPointSummarize
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


    public suspend fun dealData(): VerifyResultAllSummarize {
        return when (selectedType.value) {
            FindTargetType.RGB -> {
                dealRgbData()
            }

            FindTargetType.HSV -> {
                dealHsvData()
            }

            FindTargetType.IMG -> {
                dealImgData()
            }

            FindTargetType.MAT -> {
                dealMatData()
            }

            else -> {
                VerifyResultAllSummarize()
            }
        }
    }


    private suspend fun dealRgbData(): VerifyResultAllSummarize {
        val rgb = IdentifyDatabase.getDatabase().findTargetRgbDao().findByKeyTag(tag)
            ?: return VerifyResultAllSummarize()
        val list = IdentifyDatabase.getDatabase().targetVerifyResultDao()
            .findByTagTypeIsPassIsEffectiveIsDo(tag, FindTargetType.RGB, true, false, true)
        if (list.isNotEmpty()) {
            return VerifyResultAllSummarize()
        }
        val result = VerifyResultAllSummarize(true)
        val list2 = IdentifyDatabase.getDatabase().targetVerifyResultDao()
            .findByTagTypeIsPassIsEffectiveIsDo(tag, FindTargetType.RGB, false, false, true)
        if (list2.isNotEmpty()) {
            result.failList = rgb.prList.map { VerifyResultPointSummarize(pointRule = it) }
            list2.forEach {
                it.poinitInfo?.forEachIndexed { p, d ->
                    result.failList?.getOrNull(p)?.let { x ->
                        x.poinitInfo.add(d)
                        if (d.isPass) {
                            x.passCount += 1
                        } else {
                            x.failCount += 1
                        }
                    }
                }
            }
        }

        val list3 = IdentifyDatabase.getDatabase().targetVerifyResultDao()
            .findByTagTypeIsPassIsEffectiveIsDo(tag, FindTargetType.RGB, true, true, true)
        if (list3.isNotEmpty()) {
            result.passList = rgb.prList.map { VerifyResultPointSummarize(pointRule = it) }
            list3.forEach {
                it.poinitInfo?.forEachIndexed { p, d ->
                    result.passList?.getOrNull(p)?.let { x ->
                        x.poinitInfo.add(d)
                        if (d.isPass) {
                            x.passCount += 1
                        } else {
                            x.failCount += 1
                        }
                    }
                }
            }
        }
        return result
    }

    private fun dealHsvData(): VerifyResultAllSummarize {
        val hsv = IdentifyDatabase.getDatabase().findTargetHsvDao().findByKeyTag(tag)
            ?: return VerifyResultAllSummarize()
        val list = IdentifyDatabase.getDatabase().targetVerifyResultDao()
            .findByTagTypeIsPassIsEffectiveIsDo(tag, FindTargetType.HSV, true, false, true)
        if (list.isNotEmpty()) {
            return VerifyResultAllSummarize()
        }
        val result = VerifyResultAllSummarize(true)
        val list2 = IdentifyDatabase.getDatabase().targetVerifyResultDao()
            .findByTagTypeIsPassIsEffectiveIsDo(tag, FindTargetType.HSV, false, false, true)
        if (list2.isNotEmpty()) {
            result.failList = hsv.prList.map { VerifyResultPointSummarize(pointHSVRule = it) }
            list2.forEach {
                it.poinitInfo?.forEachIndexed { p, d ->
                    result.failList?.getOrNull(p)?.let { x ->
                        x.poinitInfo.add(d)
                        if (d.isPass) {
                            x.passCount += 1
                        } else {
                            x.failCount += 1
                        }
                    }
                }
            }
        }
        val list3 = IdentifyDatabase.getDatabase().targetVerifyResultDao()
            .findByTagTypeIsPassIsEffectiveIsDo(tag, FindTargetType.HSV, true, true, true)
        if (list3.isNotEmpty()) {
            result.passList = hsv.prList.map { VerifyResultPointSummarize(pointHSVRule = it) }
            list3.forEach {
                it.poinitInfo?.forEachIndexed { p, d ->
                    result.passList?.getOrNull(p)?.let { x ->
                        x.poinitInfo.add(d)
                        if (d.isPass) {
                            x.passCount += 1
                        } else {
                        }
                    }
                }
            }
        }
        return result
    }

    private fun dealImgData(): VerifyResultAllSummarize {
        val list = IdentifyDatabase.getDatabase().targetVerifyResultDao()
            .findByTagTypeIsPassIsEffectiveIsDo(tag, FindTargetType.IMG, true, false, true)
        if (list.isNotEmpty()) {
            return VerifyResultAllSummarize()
        }
        val result = VerifyResultAllSummarize(true)
        val list2 = IdentifyDatabase.getDatabase().targetVerifyResultDao()
            .findByTagTypeIsPassIsEffectiveIsDo(tag, FindTargetType.IMG, false, false, true)

        if (list2.isNotEmpty()) {
            result.thresholdList.addAll(list2.map { it.nowthreshold })
        }
        val list3 = IdentifyDatabase.getDatabase().targetVerifyResultDao()
            .findByTagTypeIsPassIsEffectiveIsDo(tag, FindTargetType.IMG, true, true, true)
        if (list3.isNotEmpty()) {
            result.thresholdList.addAll(list3.map { it.nowthreshold })
        }
        return result
    }


    private fun dealMatData(): VerifyResultAllSummarize {
        val list2 = IdentifyDatabase.getDatabase().targetVerifyResultDao()
            .findByTagTypeIsPassIsEffectiveIsDo(tag, FindTargetType.MAT, false, false, true)
        val list3 = IdentifyDatabase.getDatabase().targetVerifyResultDao()
            .findByTagTypeIsPassIsEffectiveIsDo(tag, FindTargetType.MAT, true, true, true)
        return if (list3.size > list2.size * 5) {
            VerifyResultAllSummarize(true)
        } else {
            VerifyResultAllSummarize()
        }
    }

}
