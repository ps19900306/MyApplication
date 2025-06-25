package com.example.myapplication.find_target


import android.view.LayoutInflater
import android.view.MenuItem
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.auto_hsv_rule.AutoHsvRuleSelectFragmentArgs
import com.example.myapplication.auto_hsv_rule.ModifyHsvDialog
import com.example.myapplication.databinding.FragmentHsvTargetDetailBinding
import com.example.myapplication.preview.PreviewViewModel
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.baseutils.T
import com.nwq.callback.CallBack
import com.nwq.constant.ConstantKeyStr
import com.nwq.dialog.SimpleInputDialog
import com.nwq.dialog.SimpleTipsDialog
import com.nwq.opencv.hsv.HSVRule
import com.nwq.opencv.hsv.PointHSVRule
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckTextWrap


/**
 * A simple [com.nwq.opencv.db.entity.FindTargetHsvEntity] 的预览.
 */
class HsvTargetDetailFragment : BaseToolBar2Fragment<FragmentHsvTargetDetailBinding>() {

    private lateinit var mCheckTextAdapter: CheckTextAdapter<PointHSVRule>
    private val viewModel: FindTargetDetailModel by viewModels({ requireActivity() })

    private val SELECT_HSV_RULE_TAG = "select_hsv_rule"
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
                viewModel.saveHsvTarget()
            }

            R.id.action_auto_rule -> {
                //选择功能区域
                findNavController().navigate(
                    R.id.action_hsvTargetDetailFragment_to_AutoHsvRuleSelectFragment,
                    AutoHsvRuleSelectFragmentArgs(SELECT_HSV_RULE_TAG).toBundle()
                )
            }

            R.id.action_create -> {
                if (viewModel.mBitmap == null) {
                    T.show("图片为空")
                } else if (viewModel.targetOriginalArea == null) {
                    T.show("请先选择区域")
                } else if (viewModel.autoRulePoint == null) {
                    T.show("请先设置自动规则")
                } else {
                    SimpleTipsDialog(
                        onClick = {
                            viewModel.performAutoFindRule(true, it)
                        }
                    ).show(childFragmentManager, "SimpleTipsDialog")
                }
            }

            R.id.delete_select -> {
                viewModel.mFindTargetHsvEntity?.prList = mCheckTextAdapter.removeSelectAndGet()
            }

            R.id.delete_modify -> {//修改最大容错
                if (viewModel.mFindTargetHsvEntity != null) {
                    SimpleInputDialog(
                        onClick = {
                            it.toIntOrNull()?.let {
                                viewModel.mFindTargetHsvEntity?.errorTolerance = it
                                updateInfo();
                            }
                        }
                    ).show(childFragmentManager, "SimpleTipsDialog")
                }
            }
        }
        return flag
    }


    private fun modifyHsv(src: PointHSVRule) {
        ModifyHsvDialog(src.rule, null, object : CallBack<HSVRule> {
            override fun onCallBack(data: HSVRule) {
                src.rule = data
            }
        })
    }

    override fun initData() {
        super.initData()
        mCheckTextAdapter = CheckTextAdapter(mLongClick = object : CallBack<PointHSVRule> {
            override fun onCallBack(data: PointHSVRule) {
                modifyHsv(data)
            }
        })
        binding.recyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = mCheckTextAdapter
        viewModel.mFindTargetHsvEntity?.let { entity ->
            val list = entity.prList.map { data ->
                ICheckTextWrap<PointHSVRule>(data) {
                    it.point.toString() + it.rule.toString()
                }
            }
            mCheckTextAdapter.upData(list)
        }
        updateInfo();
        parentFragment?.setFragmentResultListener(
            SELECT_HSV_RULE_TAG, // 这个 tag 要和 ClickSelectFragment 接收到的 args.actionTag 一致
            { requestKey, result ->
                val selectedIds = result.getLongArray(ConstantKeyStr.SELECTED_RESULT)
                selectedIds?.get(0)?.let { viewModel.updateHsvRule(it) }
            })
    }


    private fun updateInfo() {
        viewModel.mFindTargetHsvEntity?.let { entity ->
            binding.infoTv.text =
                "OriginalArea:${entity.targetOriginalArea}::findArea:${entity.findArea}::errorTolerance:${entity.errorTolerance}";
        }
    }


    override fun onBackPress(): Boolean {
        findNavController().popBackStack()
        return true
    }


}