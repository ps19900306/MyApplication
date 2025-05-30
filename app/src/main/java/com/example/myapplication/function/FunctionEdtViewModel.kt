package com.example.myapplication.function

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nwq.baseutils.runOnIO
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.FunctionEntity
import com.nwq.opencv.db.entity.LogicEntity
import com.nwq.simplelist.IText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext


class FunctionEdtViewModel() : ViewModel() {

    private val mFunctionDao = IdentifyDatabase.getDatabase().functionDao()
    private val mLogicDao = IdentifyDatabase.getDatabase().logicDao()
    public var id: Long = 0


    //这个是当前逻辑单元的
    private val _nowLogicFlow: MutableStateFlow<MutableList<LogicEntity>> =
        MutableStateFlow(mutableListOf())
    val nowLogicFlow: Flow<MutableList<LogicEntity>> = _nowLogicFlow


    val allLogicFlow: Flow<List<LogicEntity>> by lazy {
        mLogicDao.findByFunctionIdFlow(id)
    }

    //当前选中的逻辑单元
    var selectLogicEntity: LogicEntity? = null


    //触发了事件的逻辑单元
    private val _triggerLogicFlow: MutableStateFlow<MutableList<LogicEntity>> =
        MutableStateFlow(mutableListOf())
    val triggerLogicFlow: Flow<MutableList<LogicEntity>> = _triggerLogicFlow


    public fun initFunctionData(id: Long): Flow<FunctionEntity?> {
        this.id = id
        viewModelScope.runOnIO {
            val list = mLogicDao.findByFunctionIdRoot(id)
            _nowLogicFlow.tryEmit(list)
        }
        return mFunctionDao.findByKeyIdFlow(id)
    }


    suspend fun createLogic(functionId: Long, name: String, parentId: Long, priority: Int): Long {
        return withContext(Dispatchers.IO) {
            val entity = LogicEntity()
            entity.functionId = functionId
            entity.parentLogicId = parentId
            entity.priority = priority
            entity.keyTag = name
            mLogicDao.insert(entity)
        }
    }

    fun onTrigger(
        logic: LogicEntity,
        nowList: MutableList<LogicEntity>,
        AllList: List<LogicEntity>
    ) {
        _nowLogicFlow.tryEmit(logic.onHasChanged(nowList, AllList))
    }


}
