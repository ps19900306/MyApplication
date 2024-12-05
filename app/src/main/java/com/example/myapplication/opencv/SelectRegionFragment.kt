package com.example.myapplication.opencv


import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.databinding.FragmentSelectRegionBinding
import com.nwq.base.BaseFragment




class SelectRegionFragment : BaseFragment<FragmentSelectRegionBinding>() {

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSelectRegionBinding {
        return FragmentSelectRegionBinding.inflate(inflater, container, false)
    }




}