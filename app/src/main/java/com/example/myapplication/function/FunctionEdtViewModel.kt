package com.example.myapplication.function

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nwq.opencv.core.ILogicUnit
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.FunctionEntity
import com.nwq.opencv.db.entity.LogicEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest

class FunctionEdtViewModel(val id: Long) : ViewModel() {

    private val mFunctionDao = IdentifyDatabase.getDatabase().functionDao()
    private val mLogicDao = IdentifyDatabase.getDatabase().logicDao()

    //这个是当前逻辑单元的
    private val _nowLogicFlow: MutableStateFlow<MutableList<LogicEntity>> =
        MutableStateFlow(mutableListOf())
    val nowLogicFlow: Flow<MutableList<LogicEntity>> = _nowLogicFlow


    val allLogicFlow: Flow<List<LogicEntity>> by lazy {
        mLogicDao.findByFunctionId(id)
    }

    //触发了事件的逻辑单元
    private val _triggerLogicFlow: MutableStateFlow<MutableList<LogicEntity>> =
        MutableStateFlow(mutableListOf())
    val triggerLogicFlow: Flow<MutableList<LogicEntity>> = _triggerLogicFlow

    public lateinit var mFunctionEntity: FunctionEntity


    public suspend fun initData() {
        mFunctionEntity = mFunctionDao.findByKeyId(id)
        allLogicFlow.collectLatest {

        }

    }


}