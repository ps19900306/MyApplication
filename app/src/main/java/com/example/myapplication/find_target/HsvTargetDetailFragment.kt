package com.example.myapplication.find_target


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.ColorAdapter
import com.example.myapplication.auto_hsv_rule.AutoHsvRuleSelectFragmentArgs
import com.example.myapplication.auto_hsv_rule.ModifyHsvDialog
import com.example.myapplication.databinding.FragmentHsvTargetDetailBinding
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.baseobj.CoordinatePoint
import com.nwq.baseobj.PreviewCoordinateData
import com.nwq.baseutils.HsvRuleUtils
import com.nwq.baseutils.T
import com.nwq.callback.CallBack
import com.nwq.callback.CallBack2
import com.nwq.constant.ConstantKeyStr
import com.nwq.dialog.SimpleInputDialog
import com.nwq.dialog.SimpleTipsDialog
import com.nwq.loguitls.L
import com.nwq.opencv.hsv.HSVRule
import com.nwq.opencv.hsv.PointHSVRule
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.truncate


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

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("HsvTargetDetailFragment", "onDestroyView called") // 检查是否执行
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 在 onCreate 注册，避免错过回调
        Log.d("HsvTargetDetailFragment", "onCreate called ${parentFragment==null}") // 检查是否执行
        parentFragment?.setFragmentResultListener(
            SELECT_HSV_RULE_TAG,
            { requestKey, result ->
                Log.i("HsvTargetDetailFragment", "SELECT_HSV_RULE_TAG called")
                result.getString(ConstantKeyStr.SELECTED_RESULT)?.let { keyTag ->
                    viewModel.updateHsvRule(keyTag)
                }
            }
        )
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        var flag = false
        when (menuItem.itemId) {
            R.id.action_save -> {
                viewModel.saveHsvTarget()
            }
            R.id.action_select_inverse->{
                mCheckTextAdapter.selectReverse()
            }
            R.id.action_auto_rule -> {
                //选择功能区域
                findNavController().navigate(
                    R.id.action_hsvTargetDetailFragment_to_AutoHsvRuleSelectFragment,
                    AutoHsvRuleSelectFragmentArgs(SELECT_HSV_RULE_TAG,true).toBundle()
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
                    lifecycleScope.launch {
                        viewModel.performAutoFindRule(true, false)
                        Log.i("FindTargetDetailModel", "launch结束")
                        updateInfo()
                    }
//                    SimpleTipsDialog(
//                        onClick = {
//                            viewModel.performAutoFindRule(true, it)
//                        }
//                    ).show(childFragmentManager, "SimpleTipsDialog")
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
        }).show(childFragmentManager, "ModifyHsvDialog")
    }

    override fun initData() {
        super.initData()
        L.i("HsvTargetDetailFragment", "initData")
        mCheckTextAdapter = CheckTextAdapter(
            layoutId = R.layout.item_color_rule,
            textId = R.id.tv,
            mLongClick = object : CallBack<PointHSVRule> {
                override fun onCallBack(data: PointHSVRule) {
                    modifyHsv(data)
                }
            })
        mCheckTextAdapter.setBindView(object : CallBack2<View, PointHSVRule> {
            override fun onCallBack(data: View, data2: PointHSVRule) {
                val recyclerView = data.findViewById<RecyclerView>(R.id.rv)
                recyclerView.adapter = ColorAdapter(
                    HsvRuleUtils.getColorsList(
                        data2.rule.minH,
                        data2.rule.maxH,
                        data2.rule.minS,
                        data2.rule.maxS,
                        data2.rule.minV,
                        data2.rule.maxV
                    )
                )
            }
        })
        binding.recyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = mCheckTextAdapter
        updateInfo();
        initPreviewImg()
    }


    private fun initPreviewImg() {
        binding.previewLayout.root.isVisible = viewModel.getSelectBitmap() != null
        if (viewModel.getSelectBitmap() != null) {
            binding.previewLayout.imageView.setImageBitmap(viewModel.getSelectBitmap())
            binding.previewLayout.checkBox.setOnCheckedChangeListener(object :
                CompoundButton.OnCheckedChangeListener {

                override fun onCheckedChanged(p0: CompoundButton, p1: Boolean) {
                    if (p1) {
                        binding.previewLayout.imageView.isVisible = true
                    } else {
                        binding.previewLayout.imageView.isInvisible = true
                    }
                }
            })
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    mCheckTextAdapter.checkListFlow.collectLatest { d ->
                        val ponits = d.map {
                            val point = CoordinatePoint(
                                it.point.x - (viewModel.targetOriginalArea?.x ?: 0),
                                it.point.y - (viewModel.targetOriginalArea?.y ?: 0)
                            )
                            val color = Color.HSVToColor(
                                floatArrayOf(
                                    it.rule.maxH * 1.99f,
                                    it.rule.maxS / 255f,
                                    it.rule.maxV / 255f
                                )
                            )
                            PreviewCoordinateData(point, color, 3f)
                        }
                        binding.previewLayout.previewCoordinateView.updateList(ponits)
                    }
                }
            }
        }
    }


    private fun updateInfo() {
        viewModel.mFindTargetHsvEntity?.let { entity ->
            val list = entity.prList.map { data ->
                ICheckTextWrap<PointHSVRule>(data) {
                    "${it.point.toString()}\n${it.rule.toStringSimple()}"
                }
            }
            mCheckTextAdapter.upData(list)
        }
        viewModel.mFindTargetHsvEntity?.let { entity ->
            binding.infoTv.text =
                "OriginalArea:${entity.targetOriginalArea.toStringSimple()}\nfindArea:${entity.findArea?.toStringSimple()}\nerrorTolerance:${entity.errorTolerance}";
        } ?: let {
            binding.infoTv.text = "还未设置"
        }
    }


    override fun onBackPress(): Boolean {
        Log.i("HsvTargetDetailFragment", "onBackPress")
        findNavController().popBackStack()
        return true
    }


}