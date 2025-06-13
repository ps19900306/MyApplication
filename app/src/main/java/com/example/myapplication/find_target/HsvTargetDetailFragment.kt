package com.example.myapplication.find_target

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentFindTargetDetailBinding
import com.example.myapplication.databinding.FragmentHsvTargetDetailBinding
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.opencv.db.entity.FindTargetRecord
import com.nwq.opencv.hsv.HSVRule
import com.nwq.opencv.hsv.PointHSVRule
import com.nwq.simplelist.CheckTextAdapter


/**
 * A simple [com.nwq.opencv.db.entity.FindTargetHsvEntity] 的预览.
 */
class HsvTargetDetailFragment : BaseToolBar2Fragment<FragmentHsvTargetDetailBinding>() {

    private lateinit var mCheckTextAdapter: CheckTextAdapter<PointHSVRule>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hsv_target_detail, container, false)
    }

    override fun createBinding(inflater: LayoutInflater): FragmentHsvTargetDetailBinding {
        return FragmentHsvTargetDetailBinding.inflate(inflater);
    }

    override fun getMenuRes(): Int {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun onBackPress(): Boolean {
        TODO("Not yet implemented")
    }


}