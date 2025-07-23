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
import com.example.myapplication.auto_hsv_rule.AutoHsvRuleSelectFragmentArgs
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
                    viewModel.updateImgStorageHsvRule(keyTag)
                }
            }
        )

        parentFragment?.setFragmentResultListener(
            SELECT_HSV_RULE_TAG2,
            { requestKey, result ->
                L.d("ImgTargetDetailFragment", "onCreate:", "2025/7/21/15:30")
                result.getString(ConstantKeyStr.SELECTED_RESULT)?.let { keyTag ->
                    viewModel.updateImgFinalHsvRule(keyTag)
                }
            }
        )
    }


    override fun getMenuRes(): Int {
        return R.menu.menu_target_img
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        var flag = true
        when (menuItem.itemId) {
            R.id.action_save -> {
                viewModel.saveImgTarget()
            }

            R.id.action_storage_rule->{
                findNavController().navigate(
                    R.id.action_imgTargetDetailFragment_to_AutoHsvRuleSelectFragment,
                    AutoHsvRuleSelectFragmentArgs(SELECT_HSV_RULE_TAG1,true).toBundle()
                )
            }
            R.id.action_final_rule -> {
                findNavController().navigate(
                    R.id.action_imgTargetDetailFragment_to_AutoHsvRuleSelectFragment,
                    AutoHsvRuleSelectFragmentArgs(SELECT_HSV_RULE_TAG2,true).toBundle()
                )
            }
            else->{
                flag = false
            }
        }
        return flag
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