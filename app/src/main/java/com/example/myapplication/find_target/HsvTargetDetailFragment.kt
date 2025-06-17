package com.example.myapplication.find_target

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentFindTargetDetailBinding
import com.example.myapplication.databinding.FragmentHsvTargetDetailBinding
import com.example.myapplication.preview.PreviewViewModel
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.callback.CallBack
import com.nwq.opencv.db.entity.FindTargetRecord
import com.nwq.opencv.hsv.HSVRule
import com.nwq.opencv.hsv.PointHSVRule
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckText
import com.nwq.simplelist.ICheckTextWrap


/**
 * A simple [com.nwq.opencv.db.entity.FindTargetHsvEntity] 的预览.
 */
class HsvTargetDetailFragment : BaseToolBar2Fragment<FragmentHsvTargetDetailBinding>() {

    private lateinit var mCheckTextAdapter: CheckTextAdapter<PointHSVRule>
    private val viewModel: FindTargetDetailModel by viewModels({ requireActivity() })
    private val preViewModel: PreviewViewModel by viewModels({ requireActivity() })


    override fun createBinding(inflater: LayoutInflater): FragmentHsvTargetDetailBinding {
        return FragmentHsvTargetDetailBinding.inflate(inflater);
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_target_hsv
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        var flag = false
        when (menuItem.itemId) {
            R.id.action_save -> {

            }

            R.id.action_create -> {

            }

            R.id.delete_select -> {

            }

            R.id.delete_modify -> {

            }
        }
        return flag
    }

    override fun initData() {
        super.initData()
        mCheckTextAdapter = CheckTextAdapter(mLongClick = object : CallBack<PointHSVRule> {
            override fun onCallBack(data: PointHSVRule) {

            }
        })
        binding.recyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = mCheckTextAdapter
        updateInfo();
    }

    private fun updateInfo() {
        viewModel.mFindTargetHsvEntity?.let { entity ->
            val list = entity.prList.map { data ->
                ICheckTextWrap<PointHSVRule>(data) {
                    it.point.toString() + it.rule.toString()
                }
            }
            binding.infoTv.text =
                "OriginalArea:${entity.targetOriginalArea}::findArea:${entity.findArea}::errorTolerance:${entity.errorTolerance}";
        }
    }


    override fun onBackPress(): Boolean {
        findNavController().popBackStack()
        return true
    }


}