package com.example.myapplication.auto_hsv_rule

import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.HsvRuleAdapter
import com.example.myapplication.base.TouchOptModel
import com.example.myapplication.databinding.FragmentAutoHsvRuleDetailBinding
import com.example.myapplication.find_target.FindTargetDetailFragmentArgs
import com.example.myapplication.preview.PreviewOptItem
import com.example.myapplication.preview.PreviewViewModel
import com.nwq.base.BaseFragment
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.FileUtils
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.T
import com.nwq.baseutils.runOnIO
import com.nwq.baseutils.runOnUI
import com.nwq.baseutils.singleClick
import com.nwq.callback.CallBack
import com.nwq.constant.ConstantKeyStr
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.db.entity.AutoRulePointEntity
import com.nwq.opencv.hsv.HSVRule
import com.nwq.opencv.hsv.PointHSVRule
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.core.Mat


/**
 * [AutoRulePointEntity]
 */
class AutoHsvRuleDetailFragment : BaseToolBar2Fragment<FragmentAutoHsvRuleDetailBinding>() {

    private val args: AutoHsvRuleDetailFragmentArgs by navArgs()
    private val preViewModel: PreviewViewModel by viewModels({ requireActivity() })
    private val mAutoRulePointDao = IdentifyDatabase.getDatabase().autoRulePointDao()
    private lateinit var mCheckTextAdapter: CheckTextAdapter<HSVRule>
    private var prList: MutableList<HSVRule> = mutableListOf()
    private var mAutoRulePointEntity: AutoRulePointEntity? = null
    private var mBitmap: Bitmap? = null

    override fun createBinding(inflater: LayoutInflater): FragmentAutoHsvRuleDetailBinding {
        return FragmentAutoHsvRuleDetailBinding.inflate(inflater)
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_auto_hsv_rule
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        var flag = true
        when (menuItem.itemId) {
            R.id.action_save -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    mAutoRulePointEntity?.let { entity ->
                        entity.prList = prList
                        mAutoRulePointDao.update(entity)
                    }
                }
            }

            R.id.action_area -> {
                findArea()
            }

            R.id.action_select_area -> { //根据选中的Rule对图片尽心给过滤
                preViewSelectArea()
            }

            R.id.action_add -> {
                val dialog = ModifyHsvDialog(HSVRule(), getBitmap(), object : CallBack<HSVRule> {
                    override fun onCallBack(data: HSVRule) {
                        prList.add(data)
                        val list = prList.map { data ->
                            ICheckTextWrap<HSVRule>(data) {
                                it.toString()
                            }
                        }
                        mCheckTextAdapter.upData(list)
                    }
                })
                dialog.show(childFragmentManager, "ModifyHsvDialog")
            }

            R.id.action_delete_select -> {
                prList.clear()
                prList.addAll(mCheckTextAdapter.removeSelectAndGet())
            }

            R.id.action_delete_all -> {
                prList.clear()
                mCheckTextAdapter.upData(listOf())
            }

            else -> {
                flag = false
            }
        }
        return flag
    }

    override fun onBackPress(): Boolean {
        return false
    }


    override fun initData() {
        super.initData()
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        mCheckTextAdapter = CheckTextAdapter(mLongClick = object : CallBack<HSVRule> {
            override fun onCallBack(data: HSVRule) {
                val dialog = ModifyHsvDialog(HSVRule(), getBitmap(), object : CallBack<HSVRule> {
                    override fun onCallBack(data: HSVRule) {
                        mCheckTextAdapter.notifyDataSetChanged()
                    }
                })
                dialog.show(childFragmentManager, "ModifyHsvDialog")
            }
        })
        binding.recycler.adapter = mCheckTextAdapter
        lifecycleScope.launch(Dispatchers.IO) {
            mAutoRulePointEntity = mAutoRulePointDao.findByKeyId(args.autoHsvId)
            mAutoRulePointEntity?.let { entity ->
                runOnUI {
                    prList.addAll(entity.prList)
                    val list = entity.prList.map { data ->
                        ICheckTextWrap<HSVRule>(data) {
                            it.toString()
                        }
                    }
                    mCheckTextAdapter.upData(list)
                }
            }
        }
    }


    private fun getBitmap(): Bitmap? {
        if (mBitmap != null)
            return mBitmap
        if (preViewModel.mBitmap.value == null) {
            return null
        }
        preViewModel.mBitmap.value
        val co = preViewModel.getCoordinate(key = R.string.select_critical_area)
        if (co != null && co is CoordinateArea) {
            //根据区域队Bitmap进行裁剪
            mBitmap = Bitmap.createBitmap(
                preViewModel.mBitmap.value!!,
                co.x,
                co.y,
                co.width,
                co.height
            )
        }
        return mBitmap
    }

    //预览选中区域
    private fun preViewSelectArea() {
        val rules = mCheckTextAdapter.getSelectedItem()
        if (rules.isEmpty()) {
            T.show("请选择规则")
        }

        getBitmap()?.let { bitMap ->
            val mat = MatUtils.bitmapToHsvMat(bitMap)
            var lastMaskMat: Mat? = null
            rules.forEach {
                val rule = it.getT()
                val maskMat = MatUtils.getFilterMaskMat(
                    mat,
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
            val resultMap = MatUtils.hsvMatToBitmap(MatUtils.filterByMask(mat, lastMaskMat!!))
            binding.srcImg.setImageBitmap(resultMap)
        }?:let {
            T.show("请选择图片和关键区域")
        }
    }


    //选择图片和关键区域
    private fun findArea() {
        preViewModel.optList.clear();
        preViewModel.optList.add(
            PreviewOptItem(
                key = R.string.select_critical_area,
                type = TouchOptModel.RECT_AREA_TYPE,
                color = ContextCompat.getColor(requireContext(), com.nwq.baseutils.R.color.red)
            )
        )
        mBitmap = null
        findNavController().navigate(R.id.action_autoHsvRuleDetailFragment_to_nav_opt_preview)
    }

}