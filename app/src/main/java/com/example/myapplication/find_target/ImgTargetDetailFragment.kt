package com.example.myapplication.find_target

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHsvTargetDetailBinding
import com.example.myapplication.databinding.FragmentImgTargetDetailBinding
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.constant.ConstantKeyStr
import com.nwq.loguitls.L


/**
 * A simple [com.nwq.opencv.db.entity.FindTargetImgEntity] 的预览.
 */
class ImgTargetDetailFragment : BaseToolBar2Fragment<FragmentImgTargetDetailBinding>() {

    private val viewModel: FindTargetDetailModel by viewModels({ requireActivity() })

    override fun createBinding(inflater: LayoutInflater): FragmentImgTargetDetailBinding {
        return FragmentImgTargetDetailBinding.inflate(inflater)
    }

    private val SELECT_HSV_RULE_TAG1 = "select_hsv_rule1"
    private val SELECT_HSV_RULE_TAG2 = "select_hsv_rule2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragment?.setFragmentResultListener(
            SELECT_HSV_RULE_TAG1,
            { requestKey, result ->
                L.d("ImgTargetDetailFragment", "onCreate:", "2025/7/21/15:30")
                result.getString(ConstantKeyStr.SELECTED_RESULT)?.let { keyTag ->
                    viewModel.updateHsvRule(keyTag)
                }
            }
        )

        parentFragment?.setFragmentResultListener(
            SELECT_HSV_RULE_TAG2,
            { requestKey, result ->
                L.d("ImgTargetDetailFragment", "onCreate:", "2025/7/21/15:30")
                result.getString(ConstantKeyStr.SELECTED_RESULT)?.let { keyTag ->
                    viewModel.updateHsvRule(keyTag)
                }
            }
        )
    }


    override fun getMenuRes(): Int {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun onBackPress(): Boolean {
        findNavController().popBackStack()
        return true
    }


    override fun initData() {
        super.initData()
        viewModel.getSelectBitmap()?.let {
            binding.srcImg.setImageBitmap(it)
        }

    }

}