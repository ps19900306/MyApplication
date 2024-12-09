package com.example.myapplication.opencv


import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.R
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
        list.add(ResStrKeyText(R.string.full_screen))
        list.add(ResStrKeyText(R.string.start_screen))
        list.add(ResStrKeyText(R.string.select_picture))
        list.add(ResStrKeyText(R.string.take_img))
        list.add(ResStrKeyText(R.string.select_critical_area))
        list.add(ResStrKeyText(R.string.find_the_image_area))
        return list
    }
}