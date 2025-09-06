package com.nwq.autocodetool.index

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.nwq.autocodetool.R
import com.nwq.autocodetool.databinding.FragmentComplexIndexBinding
import com.nwq.autocodetool.hsv_filter.HsvFilterRuleDetailFragmentArgs
import com.nwq.autocodetool.preview.PreviewOptItem
import com.nwq.autocodetool.preview.PreviewViewModel
import com.nwq.optlib.bean.SegmentMatInfo
import com.nwq.autocodetool.segment.SegmentParameterDialog
import com.nwq.base.BaseToolBar2Fragment

import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.PreviewCoordinateData
import com.nwq.callback.CallBack
import com.nwq.callback.CallBack2
import com.nwq.constant.ConstantKeyStr
import com.nwq.loguitls.L

import com.nwq.optlib.db.bean.CropAreaDb
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.view.TouchOptView
import kotlinx.coroutines.launch

class ComplexIndexFragment : BaseToolBar2Fragment<FragmentComplexIndexBinding>() {

    private val TAG = "ComplexIndexFragment"
    private val SELECT_HSV_RULE_TAG = "select_hsv_rule"
    private val viewModel: ComplexRecognitionViewModel by viewModels({ requireActivity() })
    private val preViewModel: PreviewViewModel by viewModels({ requireActivity() })
    private val mOptItemDialog: OptItemDialog by lazy {
        OptItemDialog().setCallBack(callBack)
    }
    private lateinit var mCheckTextAdapter: CheckTextAdapter<SegmentMatInfo>


    override fun createBinding(inflater: LayoutInflater): FragmentComplexIndexBinding {
        return FragmentComplexIndexBinding.inflate(inflater)
    }


    private val callBack: CallBack<Int> by lazy {
        object : CallBack<Int> {
            override fun onCallBack(data: Int) {
                onOptItemSelect(data)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragment?.setFragmentResultListener(
            SELECT_HSV_RULE_TAG, { requestKey, result ->
                Log.i("HsvTargetDetailFragment", "SELECT_HSV_RULE_TAG called")
                result.getString(ConstantKeyStr.SELECTED_RESULT)?.let { keyTag ->
                    viewModel.addHsvRule(keyTag)
                }

            })
    }


    override fun getMenuRes(): Int {
        return R.menu.menu_complex_index
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {

        var flag = true
        when (menuItem.itemId) {
            R.id.action_select_picture -> {
                selectPicture()
            }

            R.id.action_corp_picture -> {
                cropPicture()
            }

            R.id.action_opt_list -> {
                mOptItemDialog.show(requireActivity().supportFragmentManager, "OptItemDialog")
            }

            else -> {
                flag = false
            }
        }
        return flag
    }


    private fun onOptItemSelect(i: Int) {
        when (i) {
            com.nwq.baseutils.R.string.grayscale_binarization -> {
                findNavController().navigate(
                    R.id.action_complexIndexFragment_to_grayscaleBinarizationFragment,
                    GrayscaleBinarizationFragmentArgs(false).toBundle()
                );
            }

            com.nwq.baseutils.R.string.h_s_v_binarization -> { //选择已有的过滤规则
                findNavController().navigate(
                    R.id.action_complexIndexFragment_to_hsvFilterRuleDetailFragment,
                    HsvFilterRuleDetailFragmentArgs(false).toBundle()
                );
            }

            com.nwq.baseutils.R.string.merge_and_crop -> {
                viewModel.mergeAndCrop()
            }

            com.nwq.baseutils.R.string.segment_connected_regions -> {
                SegmentParameterDialog().setDefaultParameter(viewModel.segmentParameter)
                    .setCallBack(object : CallBack<IntArray> {
                        override fun onCallBack(data: IntArray) {
                            viewModel.segmentByConnectedRegion(data)
                        }
                    }).show(requireActivity().supportFragmentManager, "SegmentParameterDialog")
            }
        }
        mOptItemDialog.dismiss()
    }


    override fun onBackPress(): Boolean {
        return false
    }

    override fun initData() {
        super.initData()

        mCheckTextAdapter = CheckTextAdapter(
            layoutId = R.layout.item_segment_info,
            textId = R.id.areaTv,
            mLongClick = object : CallBack<SegmentMatInfo> {
                override fun onCallBack(data: SegmentMatInfo) {

                }
            })
        mCheckTextAdapter.setBindView(object : CallBack2<View, SegmentMatInfo> {
            override fun onCallBack(
                data: View,
                data2: SegmentMatInfo
            ) {
                if (data2.mBitmap != null){
                    data.findViewById<ImageView>(R.id.matImg).setImageBitmap(data2.mBitmap)
                }
                if (data2.flagStr != null){
                    data.findViewById<TextView>(R.id.tagTv).text = data2.flagStr
                }
            }
        })
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = mCheckTextAdapter


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.nowBitmapFlow.collect { bmp ->
                    bmp?.let { it -> binding.imageView.setImageBitmap(it) }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.segmentAreaListFow.collect { list ->
                    if (list == null || list.isEmpty()) {
                        mCheckTextAdapter.upData(listOf())
                        binding.previewCoordinateView.updateList(listOf())
                    } else {
                        mCheckTextAdapter.upData(list)
                        binding.previewCoordinateView.updateList(
                            list.map {
                                PreviewCoordinateData(
                                    it.coordinateArea!!,
                                    ContextCompat.getColor(
                                        requireContext(),
                                        com.nwq.baseutils.R.color.red
                                    ),
                                    1f
                                )
                            }.toMutableList()
                        )
                    }
                }
            }
        }



    }


    private fun selectPicture() {
        L.i(TAG, "selectPicture")
        PictureSelector.create(requireActivity()).openSystemGallery(SelectMimeType.ofImage())
            .forSystemResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>?) {
                    L.i(TAG, "onResult")
                    result?.getOrNull(0)?.let { localMedia ->
                        val opts = BitmapFactory.Options()
                        opts.outConfig = Bitmap.Config.ARGB_8888
                        opts.inMutable = true
                        BitmapFactory.decodeFile(localMedia.realPath, opts)?.let {
                            viewModel.setNewBitmap(it)
                            preViewModel.mBitmap = it
                        }
                    }
                }

                override fun onCancel() {
                    L.i(TAG, "onCancel")
                }
            })
    }


    override fun onResume() {
        super.onResume()
        preViewModel.optList.find { it.key == com.nwq.baseutils.R.string.crop_picture }?.let {
            if (it.coordinate != null && it.coordinate is CoordinateArea) {
                val opt = CropAreaDb().apply { coordinateArea = (it.coordinate as CoordinateArea) }
                viewModel.checkAndAddOpt(opt, CropAreaDb::class.java)
                it.coordinate = null
            }
        }
        preViewModel.optList.find { it.key == com.nwq.baseutils.R.string.find_image_area }?.let {
            if (it.coordinate != null && it.coordinate is CoordinateArea) {
                viewModel.findArea = (it.coordinate as CoordinateArea)
                it.coordinate = null
            }
        }
    }

    private fun cropPicture() {
        //因为可能会多处使用到preViewModel，所以这里先清空
        preViewModel.optList.clear();
        preViewModel.optList.add(
            PreviewOptItem(
                key = com.nwq.baseutils.R.string.find_image_area,
                type = TouchOptView.RECT_AREA_TYPE,
                color = ContextCompat.getColor(requireContext(), com.nwq.baseutils.R.color.red),
                coordinate = viewModel.findArea
            )
        )
        preViewModel.optList.add(
            PreviewOptItem(
                key = com.nwq.baseutils.R.string.crop_picture,
                type = TouchOptView.RECT_AREA_TYPE,
                color = ContextCompat.getColor(requireContext(), com.nwq.baseutils.R.color.red),
                coordinate = viewModel.getCropArea()
            )
        )
        findNavController().navigate(R.id.action_any_to_previewFragment)
    }
}