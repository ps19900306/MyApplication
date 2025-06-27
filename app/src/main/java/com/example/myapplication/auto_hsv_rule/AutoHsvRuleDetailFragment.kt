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


/**
 * [AutoRulePointEntity]
 */
class AutoHsvRuleDetailFragment : BaseToolBar2Fragment<FragmentAutoHsvRuleDetailBinding>() {

    private val args: AutoHsvRuleDetailFragmentArgs by navArgs()
    private val preViewModel: PreviewViewModel by viewModels({ requireActivity() })
    private val viewModel: AutoHsvRuleDetailViewModel by viewModels({ requireActivity() })
    private val mAutoRulePointDao = IdentifyDatabase.getDatabase().autoRulePointDao()
    private lateinit var mCheckTextAdapter: CheckTextAdapter<HSVRule>
    private var prList: MutableList<HSVRule> = mutableListOf()
    private var mAutoRulePointEntity: AutoRulePointEntity? = null

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

    private var mBitmap: Bitmap? = null
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
        findNavController().navigate(R.id.action_findTargetDetailFragment_to_nav_opt_preview)
    }

//    private val TAG = "AutoHsvRuleDetailFragment"
//    private val args: AutoHsvRuleDetailFragmentArgs by navArgs()
//    private val viewModel by viewModels<AutoHsvRuleModel>({ requireActivity() })
//    private val hsvRuleAdapter = HsvRuleAdapter()
//    private var autoRulePointEntity: AutoRulePointEntity? = null
//    private var srcBitmap: Bitmap? = null
//    private val hsvList by lazy {
//        mutableListOf<HSVRule>()
//    }
//    private lateinit var adapter: HsvRuleAdapter
//
//    override fun createBinding(
//        inflater: LayoutInflater,
//        container: ViewGroup?
//    ): FragmentAutoHsvRuleDetailBinding {
//        return FragmentAutoHsvRuleDetailBinding.inflate(inflater, container, false)
//    }
//
//    override fun initData() {
//        super.initData()
//        adapter = HsvRuleAdapter()
//        if (!TextUtils.isEmpty(args.autoHsvRuleTag)) {
//            lifecycleScope.launch {
//                lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED) {
//                    viewModel.getByTagFlow(args.autoHsvRuleTag!!)?.collect {
//                        autoRulePointEntity = it
//                        initView(it)
//                    }
//                }
//            }
//        } else {
//            val bitmap =
//                FileUtils.loadBitmapFromGallery(ConstantKeyStr.AUTO_HSV_RULE_IMG_NAME) ?: return
//            binding.srcImg.setImageBitmap(bitmap)
//            srcBitmap = bitmap
//        }
//        binding.addBtn.singleClick {
//            addHsvRule()
//        }
//        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
//        binding.recycler.adapter = adapter
//        binding.saveBtn.singleClick {
//            saveHsvRule()
//        }
//    }
//
//    private fun addHsvRule() {
//        val dialog = ModifyHsvDialog(HSVRule(), srcBitmap, this)
//        dialog.show(childFragmentManager, "ModifyHsvDialog")
//    }
//
//    private fun saveHsvRule() {
//        if (autoRulePointEntity == null) {
//            val tag = binding.tagText.text
//            if (TextUtils.isEmpty(tag)){
//                T.show("请输入标签")
//                return
//            }
//            if (hsvList.isEmpty()){
//                T.show("请添加HSV规则")
//                return
//            }
//            lifecycleScope.launch {
//                runOnIO {
//                    viewModel.saveHsvRule(tag.toString(), hsvList, srcBitmap!!)
//                    runOnUI {
//                         findNavController().popBackStack()
//                    }
//                }
//            }
//        } else {
//            viewModel.updateHsvRule(autoRulePointEntity!!, hsvList)
//        }
//    }
//
//
//    private fun initView(autoRulePointEntity: AutoRulePointEntity) {
//        hsvList.addAll(autoRulePointEntity.prList)
//        hsvRuleAdapter.updateData(hsvList)
//    }
//
//    override fun onCallBack(data: HSVRule) {
//        Log.i(TAG, "添加新的HSVRule: $data")
//        hsvList.add(data)
//        adapter.updateData(hsvList)
//    }


}