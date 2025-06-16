package com.example.myapplication.find_target


import android.view.LayoutInflater
import android.view.MenuItem

import android.view.ViewGroup
import com.example.myapplication.databinding.FragmentFindTargetDetailBinding

import com.nwq.base.BaseFragment
import com.nwq.base.BaseToolBar2Fragment


/**
 * [com.nwq.opencv.db.entity.FindTargetRecord]
 */
class FindTargetDetailFragment : BaseToolBar2Fragment<FragmentFindTargetDetailBinding>() {


    override fun createBinding(inflater: LayoutInflater): FragmentFindTargetDetailBinding {
        return FragmentFindTargetDetailBinding.inflate(inflater)
    }

    override fun getMenuRes(): Int {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun onBackPress(): Boolean {
        return true
    }


}





