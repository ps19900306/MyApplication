package com.example.myapplication.find_target

import android.view.LayoutInflater
import android.view.MenuItem
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.auto_hsv_rule.AutoHsvRuleSelectFragmentArgs
import com.example.myapplication.databinding.FragmentRgbTargetDetailBinding
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.baseutils.T
import com.nwq.callback.CallBack
import com.nwq.constant.ConstantKeyStr
import com.nwq.dialog.SimpleInputDialog
import com.nwq.dialog.SimpleTipsDialog
import com.nwq.opencv.rgb.PointRule
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.launch

/**
 * A simple [com.nwq.opencv.db.entity.FindTargetRgbEntity] 的预览.
 * 默认情况下尽量使用HSV进行图色比较
 */
class RgbTargetDetailFragment : BaseToolBar2Fragment<FragmentRgbTargetDetailBinding>() {

    private lateinit var mCheckTextAdapter: CheckTextAdapter<PointRule>
    private val viewModel: FindTargetDetailModel by viewModels({ requireActivity() })

    private val SELECT_HSV_RULE_TAG = "select_hsv_rule"
    override fun createBinding(inflater: LayoutInflater): FragmentRgbTargetDetailBinding {
        return FragmentRgbTargetDetailBinding.inflate(inflater);
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_target_hsv
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        var flag = false
        when (menuItem.itemId) {
            R.id.action_save -> {
                viewModel.saveRbgTarget()
            }

            R.id.action_auto_rule -> {
                //选择功能区域
                findNavController().navigate(
                    R.id.action_hsvTargetDetailFragment_to_AutoHsvRuleSelectFragment,
                    AutoHsvRuleSelectFragmentArgs(SELECT_HSV_RULE_TAG).toBundle()
                )
            }

            R.id.action_create -> {
                if (viewModel.mSrcBitmap == null) {
                    T.show("图片为空")
                } else if (viewModel.targetOriginalArea == null) {
                    T.show("请先选择区域")
                } else if (viewModel.autoRulePoint == null) {
                    T.show("请先设置自动规则")
                } else {
                    lifecycleScope.launch{
                        viewModel.performAutoFindRule(false,true )
                    }

//                    SimpleTipsDialog(
//                        onClick = {
//                            viewModel.performAutoFindRule(true, it)
//                        }
//                    ).show(childFragmentManager, "SimpleTipsDialog")
                }
            }

            R.id.delete_select -> {
                viewModel.mFindTargetRgbEntity?.prList = mCheckTextAdapter.removeSelectAndGet()
            }

            R.id.delete_modify -> {//修改最大容错
                if (viewModel.mFindTargetRgbEntity != null) {
                    SimpleInputDialog(
                        onClick = {
                            it.toIntOrNull()?.let {
                                viewModel.mFindTargetRgbEntity?.errorTolerance = it
                                updateInfo();
                            }
                        }
                    ).show(childFragmentManager, "SimpleTipsDialog")
                }
            }
        }
        return flag
    }


    private fun modifyRgb(src: PointRule) {
        ColorRuleEditDialog(src.rule) {
            src.rule = it
        }
    }

    override fun initData() {
        super.initData()
        mCheckTextAdapter = CheckTextAdapter(mLongClick = object : CallBack<PointRule> {
            override fun onCallBack(data: PointRule) {
                modifyRgb(data)
            }
        })
        binding.recyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = mCheckTextAdapter
        viewModel.mFindTargetRgbEntity?.let { entity ->
            val list = entity.prList.map { data ->
                ICheckTextWrap<PointRule>(data) {
                    "${it.point}\n${it.rule.toSimpleString()}"
                }
            }
            mCheckTextAdapter.upData(list)
        }
        updateInfo();
        parentFragment?.setFragmentResultListener(
            SELECT_HSV_RULE_TAG, // 这个 tag 要和 ClickSelectFragment 接收到的 args.actionTag 一致
            { requestKey, result ->
                result.getString(ConstantKeyStr.SELECTED_RESULT)?.let {keyTag->
                    viewModel.updateHsvRule(keyTag)
                }
            })
    }


    private fun updateInfo() {
        viewModel.mFindTargetRgbEntity?.let { entity ->
            binding.infoTv.text =
                "OriginalArea:${entity.targetOriginalArea}::findArea:${entity.findArea}::errorTolerance:${entity.errorTolerance}";
        }?:let{
            binding.infoTv.text = "还未设置"
        }
    }


    override fun onBackPress(): Boolean {
        findNavController().popBackStack()
        return true
    }


}