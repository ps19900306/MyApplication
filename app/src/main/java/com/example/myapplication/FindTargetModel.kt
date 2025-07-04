package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.FindTargetRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FindTargetModel : ViewModel() {

    private val queryFlow: MutableStateFlow<String> = MutableStateFlow("")
    private val mTargetRecordDao = IdentifyDatabase.getDatabase().findTargetRecordDao()


    private val mTargetRgbDao = IdentifyDatabase.getDatabase().findTargetRgbDao()
    private val mTargetHsvDao = IdentifyDatabase.getDatabase().findTargetHsvDao()
    private val mTargetImgDao = IdentifyDatabase.getDatabase().findTargetImgDao()
    private val mTargetMatDao = IdentifyDatabase.getDatabase().findTargetMatDao()


    // 合并查询逻辑
    val resultsFlow = queryFlow.debounce(1000).flatMapLatest { query ->
        if (query.isEmpty()) {
            mTargetRecordDao.findAll() // 如果输入为空，查询整个表
        } else {
            mTargetRecordDao.findByKeyTagLike(query) // 如果输入不为空，进行模糊查询
        }
    }.flowOn(Dispatchers.IO)

    fun updateSearchStr(string: String) {
        queryFlow.value = string
    }

    suspend fun createTarget(name: String, description: String): Long {
        return withContext(Dispatchers.IO) {
            val entity = FindTargetRecord(keyTag = name, description = description)
            mTargetRecordDao.insert(entity)
        }
    }

    fun delete(map: List<FindTargetRecord>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mTargetRecordDao.delete(map.toTypedArray())
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mTargetRecordDao.deleteAll()
            }
        }
    }


}