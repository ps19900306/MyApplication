package com.nwq.autocodetool.hsv_filter


import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.graphics.get
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.opencv.core.Mat


/**
 * [AutoRulePointEntity]
 */
class HsvFilterRuleDetailFragment : AppToolBarFragment<FragmentHsvFilterRuleDetailBinding>() {
    private val TAG = HsvFilterRuleDetailFragment::class.java.simpleName
    private val args: HsvFilterRuleDetailFragmentArgs by navArgs()

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
                val list = mCheckTextAdapter.list.map { it.getT() }
                val hsvFilterRuleDb = HsvFilterRuleDb().apply {
                    this.ruleList = list
                }
                viewModel.checkAndAddOpt(hsvFilterRuleDb, HsvFilterRuleDb::class.java)
                onBackPress()
            }


            R.id.action_area -> {
                addByArea()
            }

            R.id.action_point -> {

                addByPoint()
            }

            R.id.action_select_area -> { //根据选中的Rule对图片尽心给过滤
                preViewSelectArea()
            }

            R.id.action_delete_select -> {
                mCheckTextAdapter.removeSelectAndGet()
            }

            R.id.action_delete_all -> {
                mCheckTextAdapter.upData(listOf())
            }

            R.id.action_merge_select -> {
                mergeSelect()
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


    override fun initData() {
        super.initData()
        Log.i(TAG, "initData");
        lifecycleScope.launch {
            binding.previewCoordinateView.nowPoint.collectLatest {
                val bitmap = srcBitMap ?: return@collectLatest
                val color = bitmap[it.x, it.y]
                // opts.outConfig = Bitmap.Config.ARGB_8888
                binding.draggableTextView.setBackgroundColor(color)
                binding.draggableTextView.text = "(${it.x},${it.y})"
            }
        }

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

        binding.srcImg.setImageBitmap(srcBitMap)

    }


    //预览选中区域
    private fun preViewSelectArea() {
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


    private fun addByPoint() {
        if (hsvMat == null) {
            return
        }
        lifecycleScope.launch {
            binding.draggableTextView.isVisible = true
            val point = binding.previewCoordinateView.getPoint()
            MatUtils.getHsv(hsvMat!!, point.x, point.y)?.let { da ->
                val pointHSVRule =
                    HSVRule.getSimple(da[0].toInt(), da[1].toInt(), da[2].toInt())
                mCheckTextAdapter.addData(ICheckTextWrap<HSVRule>(pointHSVRule) {
                    it.toStringSimple()
                })
            }
            binding.draggableTextView.isVisible = false
        }
    }

    private fun addByArea() {
        if (hsvMat == null) {
            return
        }
        lifecycleScope.launch {
            val area = binding.previewCoordinateView.getRectArea()
            MatUtils.getHsv(hsvMat!!, area.x, area.y, area.width, area.height)
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
                        it.toStringSimple()
                    })
                }
        }
    }

}