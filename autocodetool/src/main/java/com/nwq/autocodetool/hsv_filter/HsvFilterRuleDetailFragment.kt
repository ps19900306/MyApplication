package com.nwq.autocodetool.hsv_filter


import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.nwq.autocodetool.AppToolBarFragment
import com.nwq.autocodetool.R
import com.nwq.autocodetool.databinding.FragmentHsvFilterRuleDetailBinding
import com.nwq.autocodetool.index.ComplexRecognitionViewModel
import com.nwq.autocodetool.preview.PreviewOptItem
import com.nwq.autocodetool.preview.PreviewViewModel
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.CoordinatePoint
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.T
import com.nwq.callback.CallBack
import com.nwq.optlib.bean.HSVRule
import com.nwq.optlib.db.bean.HsvFilterRuleDb
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckTextWrap
import com.nwq.view.TouchOptView
import org.opencv.core.Mat


/**
 * [AutoRulePointEntity]
 */
class HsvFilterRuleDetailFragment : AppToolBarFragment<FragmentHsvFilterRuleDetailBinding>() {

    private val TAG = HsvFilterRuleDetailFragment::class.java.simpleName
    private val args: HsvFilterRuleDetailFragmentArgs by navArgs()
    private val preViewModel: PreviewViewModel by viewModels({ requireActivity() })
    private val viewModel: ComplexRecognitionViewModel by viewModels({ requireActivity() })

    private val hsvMat: Mat? by lazy {
        viewModel.getHsvMat(args.isModify)
    }

    private val srcBitMap: Bitmap? by lazy {
        hsvMat?.let { MatUtils.hsvMatToBitmap(it) }
    }

    private lateinit var mCheckTextAdapter: CheckTextAdapter<HSVRule>

    override fun createBinding(inflater: LayoutInflater): FragmentHsvFilterRuleDetailBinding {
        return FragmentHsvFilterRuleDetailBinding.inflate(inflater)
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_filter_rule
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        var flag = true
        when (menuItem.itemId) {

            R.id.action_save -> {
                val list = mCheckTextAdapter.getSelectedItem().map { it.getT() }
                val hsvFilterRuleDb = HsvFilterRuleDb().apply {
                    this.ruleList = list
                }
                viewModel.checkAndAddOpt(hsvFilterRuleDb, HsvFilterRuleDb::class.java)
                onBackPress()
            }

            R.id.action_merge_select -> {
                mergeSelect()
            }

            R.id.action_area -> {
                findArea()
            }

            R.id.action_select_area -> { //根据选中的Rule对图片尽心给过滤
                preViewSelectArea()
            }

            R.id.action_add -> {
                val dialog =
                    ModifyHsvDialog(
                        HSVRule(),
                        srcBitMap,
                        object : CallBack<HSVRule> {
                            override fun onCallBack(data: HSVRule) {
                                mCheckTextAdapter.addData(ICheckTextWrap<HSVRule>(data) {
                                    it.toString()
                                })
                            }
                        })
                dialog.show(childFragmentManager, "ModifyHsvDialog")
            }

            R.id.action_delete_select -> {
                mCheckTextAdapter.removeSelectAndGet()
            }

            R.id.action_delete_all -> {
                mCheckTextAdapter.upData(listOf())
            }

            else -> {
                flag = false
            }
        }
        return flag
    }

    //合并选中的 过滤规则
    private fun mergeSelect() {
        val list = mCheckTextAdapter.getSelectedItem().map { it.getT() }
        if (list.isEmpty() || list.size == 1)
            return
        val minH = list.minByOrNull { it.minH }?.minH ?: 0
        val maxH = list.maxByOrNull { it.maxH }?.maxH ?: 180
        val minS = list.minByOrNull { it.minS }?.minS ?: 0
        val maxS = list.maxByOrNull { it.maxS }?.maxS ?: 255
        val minV = list.minByOrNull { it.minV }?.minV ?: 0
        val maxV = list.maxByOrNull { it.maxV }?.maxV ?: 255
        val list2 = mCheckTextAdapter.removeSelectAndGet2().toMutableList()
        val rule = HSVRule(minH, maxH, minS, maxS, minV, maxV)
        list2.add(ICheckTextWrap<HSVRule>(rule) {
            it.toString()
        })
        mCheckTextAdapter.upData(list2)
    }


    override fun onBackPress(): Boolean {
        return false
    }

    override fun onResume() {
        super.onResume()
        if (preViewModel.mBitmap == null) {
            return
        }
        //增加一个新选取点颜色
        preViewModel.optList.find { it.key == com.nwq.baseutils.R.string.select_point_hsv }
            ?.let { item ->
                preViewModel.getSrcMat()?.let { mat ->
                    val point = item.coordinate
                    if (point != null && point is CoordinatePoint) {
                        MatUtils.getHsv(mat, point.x, point.y)?.let { da ->
                            val pointHSVRule =
                                HSVRule.getSimple(da[0].toInt(), da[1].toInt(), da[2].toInt())
                            mCheckTextAdapter.addData(ICheckTextWrap<HSVRule>(pointHSVRule) {
                                it.toString()
                            })
                            item.coordinate = null
                        }
                    }
                }
            }
        //增加一个新选取点颜色
        preViewModel.optList.find { it.key == com.nwq.baseutils.R.string.select_area_hsv }
            ?.let { item ->
                preViewModel.getSrcMat()?.let { mat ->
                    val point = item.coordinate
                    if (point != null && point is CoordinateArea) {
                        MatUtils.getHsv(mat, point.x, point.y, point.width, point.height)
                            ?.let { da ->
                                val pointHSVRule =
                                    HSVRule(
                                        da[0].toInt(),
                                        da[1].toInt(),
                                        da[2].toInt(),
                                        da[3].toInt(),
                                        da[4].toInt(),
                                        da[5].toInt()
                                    )
                                mCheckTextAdapter.addData(ICheckTextWrap<HSVRule>(pointHSVRule) {
                                    it.toString()
                                })
                                item.coordinate = null
                            }
                    }
                }
            }
    }


    override fun initData() {
        super.initData()
        Log.i(TAG, "initData");


        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        mCheckTextAdapter = CheckTextAdapter(mLongClick = object : CallBack<HSVRule> {
            override fun onCallBack(data: HSVRule) {
                val dialog =
                    ModifyHsvDialog(
                        data,
                        bitmap = srcBitMap,
                        object : CallBack<HSVRule> {
                            override fun onCallBack(data: HSVRule) {
                                mCheckTextAdapter.notifyDataSetChanged()
                            }
                        })
                dialog.show(childFragmentManager, "ModifyHsvDialog")
            }
        })
        binding.recycler.adapter = mCheckTextAdapter


    }


    //预览选中区域
    private fun preViewSelectArea() {
        if (binding.srcImg.isVisible) {
            binding.srcImg.isVisible = false
            return
        }
        binding.srcImg.isVisible = true
        val rules = mCheckTextAdapter.getSelectedItem()
        if (rules.isEmpty()) {
            T.show("请选择规则")
        }
        hsvMat?.let {
            var lastMaskMat: Mat? = null
            rules.forEach {
                val rule = it.getT()
                val maskMat = MatUtils.getFilterMaskMat(
                    hsvMat!!,
                    rule.minH,
                    rule.maxH,
                    rule.minS,
                    rule.maxS,
                    rule.minV,
                    rule.maxV
                )
                if (lastMaskMat == null) {
                    lastMaskMat = maskMat
                } else {
                    lastMaskMat = MatUtils.mergeMaskMat(lastMaskMat!!, maskMat)
                }
            }
            val resultMap = MatUtils.hsvMatToBitmap(MatUtils.filterByMask(hsvMat!!, lastMaskMat!!))
            binding.srcImg.setImageBitmap(resultMap)
        }
    }


    //选择图片和关键区域
    private fun findArea() {
        preViewModel.optList.clear()
        preViewModel.optList.add(
            PreviewOptItem(
                key = com.nwq.baseutils.R.string.select_point_hsv,
                type = TouchOptView.SINGLE_CLICK_TYPE,
                color = ContextCompat.getColor(
                    requireContext(),
                    com.nwq.baseutils.R.color.black
                )
            )
        )
        preViewModel.optList.add(
            PreviewOptItem(
                key = com.nwq.baseutils.R.string.select_area_hsv,
                type = TouchOptView.RECT_AREA_TYPE,
                color = ContextCompat.getColor(
                    requireContext(),
                    com.nwq.baseutils.R.color.black
                )
            )
        )
        preViewModel.mBitmap = srcBitMap
        findNavController().navigate(R.id.action_any_to_previewFragment)
    }

}