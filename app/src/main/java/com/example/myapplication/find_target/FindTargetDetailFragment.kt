package com.example.myapplication.find_target


import android.view.LayoutInflater

import android.view.ViewGroup
import com.example.myapplication.databinding.FragmentFindTargetDetailBinding

import com.nwq.base.BaseFragment


//测
class FindTargetDetailFragment : BaseFragment<FragmentFindTargetDetailBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFindTargetDetailBinding {
        return FragmentFindTargetDetailBinding.inflate(inflater, container, false)
    }



}





