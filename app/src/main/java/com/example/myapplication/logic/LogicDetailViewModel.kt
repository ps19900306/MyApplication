package com.example.myapplication.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nwq.baseutils.ContextUtils
import com.nwq.baseutils.T
import com.nwq.baseutils.runOnIO
import com.nwq.opencv.constant.LogicJudeResult
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.ClickEntity
import com.nwq.opencv.db.entity.FunctionEntity
import com.nwq.opencv.db.entity.LogicEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class LogicDetailViewModel() : ViewModel() {

    private val mLogicDao by lazy {
        IdentifyDatabase.getDatabase().logicDao()
    }
    private val mFunctionDao by lazy {
        IdentifyDatabase.getDatabase().functionDao()
    }
    private var mLogicEntity: LogicEntity? = null
    private val mClickDao by lazy { IdentifyDatabase.getDatabase().clickDao() }

    public val mClickEntityFlow: MutableStateFlow<ClickEntity?> = MutableStateFlow(null)
    public val mStartFunctionFlow: MutableStateFlow<FunctionEntity?> = MutableStateFlow(null)
    public val mAddLogicListFow: MutableStateFlow<MutableList<LogicEntity>?> =
        MutableStateFlow(null)
    public val mDeleteLogicListFow: MutableStateFlow<MutableList<LogicEntity>?> =
        MutableStateFlow(null)

    public val items by lazy {
        listOf(
            getString(com.nwq.baseutils.R.string.normal),
            getString(com.nwq.baseutils.R.string.enable_sub_functions),
            getString(com.nwq.baseutils.R.string.function_end),
            getString(com.nwq.baseutils.R.string.complete)
        )
    }

    private fun getString(id: Int): String {
        return ContextUtils.getContext().getString(id)
    }

    private val mResultCode by lazy {
        listOf(
            LogicJudeResult.NORMAL,
            LogicJudeResult.ENABLE_SUB_FUNCTIONS,
            LogicJudeResult.FUNCTION_END,
            LogicJudeResult.COMPLETE,
        )
    }

    public fun updateConsecutiveEntries(int: Int) {
        mLogicEntity?.consecutiveEntries = int
    }


    suspend fun initLogicEntity(id: Long): LogicEntity? {
        return withContext(Dispatchers.IO) {
            mLogicEntity = mLogicDao.findByKeyId(id)
            if ((mLogicEntity?.functionId ?: 0) > 0) {
                mClickEntityFlow.tryEmit(mClickDao.findByKeyId(mLogicEntity?.findTagId ?: 0))
            }

            if ((mLogicEntity?.nextFunctionId ?: 0) > 0) {
                mStartFunctionFlow.tryEmit(
                    mFunctionDao.findByKeyId(
                        mLogicEntity?.nextFunctionId ?: 0
                    )
                )
            }

            val addList = mutableListOf<LogicEntity>()
            mLogicEntity?.addLogicList?.forEach { id ->
                mLogicDao.findByKeyId(id)?.let { addList.add(it) }
            }
            mAddLogicListFow.tryEmit(addList)
            val deleteList = mutableListOf<LogicEntity>()
            mLogicEntity?.clearLogicList?.forEach { id ->
                mLogicDao.findByKeyId(id)?.let { deleteList.add(it) }
            }
            mLogicEntity
        }
    }

    public fun addAddLogic(selectedIds: LongArray?) {
        if (selectedIds == null) {
            return
        }
        viewModelScope.runOnIO {
            val list = mAddLogicListFow.value ?: mutableListOf()
            selectedIds.forEach { newId ->
                if (list.find { newId == it.id } == null) {
                    mLogicDao.findByKeyId(newId)?.let { list.add(it) }
                }
            }
            mAddLogicListFow.tryEmit(list)
        }
    }

    public fun addClearLogic(selectedIds: LongArray?) {
        if (selectedIds == null) {
            return
        }
        viewModelScope.runOnIO {
            val list = mDeleteLogicListFow.value ?: mutableListOf()
            selectedIds.forEach { newId ->
                if (list.find { newId == it.id } == null) {
                    mLogicDao.findByKeyId(newId)?.let { list.add(it) }
                }
            }
            mDeleteLogicListFow.tryEmit(list)
        }
    }


    public fun updateClickEntity(id: Long) {
        viewModelScope.runOnIO {
            mClickEntityFlow.tryEmit(mClickDao.findByKeyId(id))
        }
    }

    public fun updatesStartFunction(id: Long) {
        viewModelScope.runOnIO {
            mStartFunctionFlow.tryEmit(mFunctionDao.findByKeyId(id))
        }
    }

    public fun updatesFindTarget(id: Long) {
        mLogicEntity?.findTagId = id
    }


    fun setResultSelection(p: Int): Boolean {
        mLogicEntity?.judeOnFindResult = mResultCode[p]
        return mResultCode[p] == LogicJudeResult.ENABLE_SUB_FUNCTIONS
    }

    fun getResultSelection(): Int {
        when (mLogicEntity?.judeOnFindResult) {
            LogicJudeResult.NORMAL -> {
                return 0
            }

            LogicJudeResult.ENABLE_SUB_FUNCTIONS -> {
                return 1
            }

            LogicJudeResult.FUNCTION_END -> {
                return 2
            }

            LogicJudeResult.COMPLETE -> {
                return 3
            }

            else -> {
                return 0
            }
        }
    }

    fun saveAll() {
        if (mLogicEntity?.findTagId == 0L) {
            T.show("请选择查找目标")
            return
        }
        mLogicEntity?.let { logicEntity ->
            logicEntity.clearLogicList = mDeleteLogicListFow.value?.map { it.id } ?: listOf()
            logicEntity.addLogicList = mAddLogicListFow.value?.map { it.id } ?: listOf()
            logicEntity.clickId = mClickEntityFlow.value?.id ?: 0
            logicEntity.nextFunctionId = mStartFunctionFlow.value?.id ?: 0
            viewModelScope.runOnIO {
                mLogicDao.update(logicEntity)
            }
        }
    }
}