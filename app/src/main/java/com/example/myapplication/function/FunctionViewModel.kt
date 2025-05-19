package com.example.myapplication.function

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nwq.baseutils.FileUtils
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.runOnIO
import com.nwq.baseutils.runOnUI
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.AutoRulePointEntity
import com.nwq.opencv.db.entity.FunctionEntity
import com.nwq.opencv.hsv.HSVRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FunctionViewModel : ViewModel() {


    private val mFunctionDao = IdentifyDatabase.getDatabase().functionDao()

    private val queryFlow: MutableStateFlow<String> = MutableStateFlow("")


    // 合并查询逻辑
    val resultsFlow = queryFlow.debounce(1000).flatMapLatest { query ->
        if (query.isEmpty()) {
            mFunctionDao.findAll() // 如果输入为空，查询整个表
        } else {
            mFunctionDao.findByKeyTagLike(query) // 如果输入不为空，进行模糊查询
        }
    }.flowOn(Dispatchers.IO)

    fun updateSearchStr(string: String) {
        queryFlow.value = string
    }

    suspend fun createFunction(name: String, description: String): Long {
        return withContext(Dispatchers.IO) {
            val entity = FunctionEntity(keyTag = name, description = description)
            mFunctionDao.insert(entity)
        }
    }

    fun delete(map: List<FunctionEntity>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mFunctionDao.delete(map.toTypedArray())
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mFunctionDao.deleteAll()
            }
        }
    }
}