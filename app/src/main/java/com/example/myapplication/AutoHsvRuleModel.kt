package com.example.myapplication

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nwq.baseutils.FileUtils
import com.nwq.baseutils.MatUtils
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.AutoRulePointEntity
import com.nwq.opencv.db.entity.FindTargetRecord
import com.nwq.opencv.hsv.HSVRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [AutoRulePointEntity]
 */
class AutoHsvRuleModel : ViewModel() {
    private val queryFlow: MutableStateFlow<String> = MutableStateFlow("")

    private val mAutoRulePointDao = IdentifyDatabase.getDatabase().autoRulePointDao()
    var nowHsv: HSVRule? = null

    // 合并查询逻辑
    val resultsFlow = queryFlow.debounce(1000).flatMapLatest { query ->
        if (query.isEmpty()) {
            mAutoRulePointDao.findAll() // 如果输入为空，查询整个表
        } else {
            mAutoRulePointDao.findByKeyTagLike(query) // 如果输入不为空，进行模糊查询
        }
    }.flowOn(Dispatchers.IO)

    fun updateSearchStr(string: String) {
        queryFlow.value = string
    }


    suspend fun saveHsvRule(tag: String, hsvRule: List<HSVRule>, bitmap: Bitmap) {
        FileUtils.saveBitmapToGalleryRule(bitmap, tag);
        val data = AutoRulePointEntity()
        data.keyTag = tag
        data.prList = hsvRule
        mAutoRulePointDao.insert(data)
    }

    fun updateHsvRule(entity: AutoRulePointEntity, hsvRule: List<HSVRule>) {
        viewModelScope.launch(Dispatchers.IO) {
            entity.prList = hsvRule
            mAutoRulePointDao.update(entity)
        }
    }


    suspend fun createHsvRule(name: String, description: String): Long {
        return withContext(Dispatchers.IO) {
            val entity = AutoRulePointEntity(keyTag = name, description = description)
            mAutoRulePointDao.insert(entity)
        }
    }

    fun delete(map: List<AutoRulePointEntity>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mAutoRulePointDao.delete(map.toTypedArray())
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mAutoRulePointDao.deleteAll()
            }
        }
    }

}