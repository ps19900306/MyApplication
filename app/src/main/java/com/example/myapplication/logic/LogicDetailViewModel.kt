package com.example.myapplication.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nwq.baseutils.ContextUtils
import com.nwq.baseutils.runOnIO
import com.nwq.opencv.constant.LogicJudeResult
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.ClickEntity
import com.nwq.opencv.db.entity.FunctionEntity
import com.nwq.opencv.db.entity.LogicEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LogicDetailViewModel() : ViewModel() {

    private val mLogicDao = IdentifyDatabase.getDatabase().logicDao()
    private var mLogicEntity: LogicEntity? = null
    private val mClickDao by lazy { IdentifyDatabase.getDatabase().clickDao() }
    public var mClickEntity: ClickEntity? = null


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

    public


    suspend fun initLogicEntity(id: Long): LogicEntity? {
        return withContext(Dispatchers.IO) {
            mLogicEntity = mLogicDao.findByKeyId(id)
            if ((mLogicEntity?.functionId ?: 0) > 0) {
                mClickEntity = mClickDao.findByKeyId(mLogicEntity?.findTagId ?: 0)
            }
            mLogicEntity
        }
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
}