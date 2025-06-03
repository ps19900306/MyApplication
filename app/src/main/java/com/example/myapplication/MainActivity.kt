package com.example.myapplication


import android.view.LayoutInflater
import com.example.myapplication.databinding.ActivityMainBinding
import com.nwq.base.BaseActivity


class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun initData() {
        TODO("Not yet implemented")
    }

    override fun createBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }


}