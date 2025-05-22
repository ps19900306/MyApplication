package com.example.myapplication.logic

import com.nwq.opencv.db.IdentifyDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest

class LogicSelectViewModel {

    private val mLogicDao = IdentifyDatabase.getDatabase().logicDao()


}