package com.example.myapplication.auto_hsv_rule

import androidx.lifecycle.ViewModel
import com.nwq.opencv.db.IdentifyDatabase

class AutoHsvRuleDetailViewModel : ViewModel() {

    private val mAutoRulePointDao = IdentifyDatabase.getDatabase().autoRulePointDao()


}