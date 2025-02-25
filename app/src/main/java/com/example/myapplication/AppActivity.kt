package com.example.myapplication

import androidx.activity.viewModels
import androidx.viewbinding.ViewBinding
import com.example.myapplication.opencv.TouchOptModel
import com.nwq.base.BaseActivity

abstract class AppActivity<VB : ViewBinding> : BaseActivity<VB>() {

    protected val mTouchOptModel by viewModels<TouchOptModel>()


    override fun beforeInitData() {
        super.beforeInitData()

    }
}