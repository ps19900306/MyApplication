package com.example.myapplication.logic

import androidx.lifecycle.ViewModel
import com.nwq.opencv.db.IdentifyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn

class LogicSelectViewModel : ViewModel (){

    private val mLogicDao = IdentifyDatabase.getDatabase().logicDao()

    public var id: Long = 0 //funcitonId

    //这个是用来进行查询的
    private val queryFlow: MutableStateFlow<String> = MutableStateFlow("")

    val logicSearchFlow = queryFlow.debounce(1000).flatMapLatest { query ->
        if (id > 0) {
            if (query.isEmpty()) {
                mLogicDao.findByFunctionIdFlow(id) // 如果输入为空，查询整个表
            } else {
                mLogicDao.findByKeyTagFunctionIdLike(query, id) // 如果输入不为空，进行模糊查询
            }
        } else {
            if (query.isEmpty()) {
                mLogicDao.findAll() // 如果输入为空，查询整个表
            } else {
                mLogicDao.findByKeyTagLike(query) // 如果输入不为空，进行模糊查询
            }
        }
    }.flowOn(Dispatchers.IO)

    fun updateLogicSearchStr(query: String) {
        queryFlow.tryEmit(query)
    }
}