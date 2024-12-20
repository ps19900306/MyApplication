package com.example.myapplication.auto_hsv_rule

import androidx.lifecycle.ViewModel
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.AutoRulePointEntity
import com.nwq.opencv.hsv.HSVRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

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

    fun getByTagFlow(tag: String): Flow<AutoRulePointEntity>? {
        return mAutoRulePointDao.findByKeyTagFlow(tag);
    }


}