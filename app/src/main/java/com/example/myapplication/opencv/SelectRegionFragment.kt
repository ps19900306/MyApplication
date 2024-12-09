package com.example.myapplication.opencv


import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.databinding.FragmentSelectRegionBinding
import com.nwq.adapter.ResStrKeyText
import com.nwq.base.BaseFragment




class SelectRegionFragment : BaseFragment<FragmentSelectRegionBinding>() {



    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSelectRegionBinding {
        return FragmentSelectRegionBinding.inflate(inflater, container, false)
    }

    override fun initData() {

    }

    private fun getList():List<ResStrKeyText>{
        val  list = mutableListOf<ResStrKeyText>()
        list.add(R.string,)
        return list
    }
}