package com.example.myapplication.function

import androidx.lifecycle.ViewModel
import com.nwq.opencv.db.IdentifyDatabase

class FunctionEdtViewModel(val id: Long) : ViewModel() {

    private val mFunctionDao = IdentifyDatabase.getDatabase().functionDao()
    private val mLogicDao = IdentifyDatabase.getDatabase().logicDao()


}