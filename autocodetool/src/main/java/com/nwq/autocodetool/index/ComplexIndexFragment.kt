package com.nwq.autocodetool.index

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle

import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.nwq.autocodetool.R
import com.nwq.autocodetool.databinding.FragmentComplexIndexBinding
import com.nwq.autocodetool.preview.PreviewViewModel
import com.nwq.base.BaseToolBar2Fragment

import com.nwq.baseobj.CoordinateArea
import com.nwq.callback.CallBack
import com.nwq.constant.ConstantKeyStr
import com.nwq.loguitls.L

import com.nwq.optlib.db.bean.CropAreaDb
import kotlinx.coroutines.launch

class ComplexIndexFragment : BaseToolBar2Fragment<FragmentComplexIndexBinding>() {

    private val TAG = "ComplexIndexFragment"
    private val SELECT_HSV_RULE_TAG = "select_hsv_rule"
    private val viewModel: ComplexRecognitionViewModel by viewModels({ requireActivity() })
    private val preViewModel: PreviewViewModel by viewModels({ requireActivity() })
    private val mOptItemDialog: OptItemDialog by lazy {
        OptItemDialog().setCallBack(callBack)
    }

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
//                findNavController().navigate(
//                    R.id.action_complexIndexFragment_to_grayscaleBinarizationFragment,
//                    GrayscaleBinarizationFragmentArgs(false).toBundle()
//                );
            }

            com.nwq.baseutils.R.string.h_s_v_binarization -> { //选择已有的过滤规则
//                findNavController().navigate(
//                    R.id.action_complexIndexFragment_to_nav_select_hsv,
//                    AutoHsvRuleSelectFragmentArgs(SELECT_HSV_RULE_TAG, false).toBundle()
//                );

            }

            com.nwq.baseutils.R.string.h_s_v_binarization_c -> {//新建新的过滤规则

            }

            com.nwq.baseutils.R.string.merge_and_crop -> {
                viewModel.mergeAndCrop()
            }
        }
        mOptItemDialog.dismiss()
    }


    override fun onBackPress(): Boolean {
        return false
    }

    override fun initData() {
        super.initData()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.nowBitmapFlow.collect { bmp ->
                    bmp?.let { it -> binding.imageView.setImageBitmap(it) }
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
    }

    private fun cropPicture() {
        //因为可能会多处使用到preViewModel，所以这里先清空
//        preViewModel.optList.clear();
//        preViewModel.optList.add(
//            PreviewOptItem(
//                key = com.nwq.baseutils.R.string.crop_picture,
//                type = TouchOptModel.RECT_AREA_TYPE,
//                color = ContextCompat.getColor(requireContext(), com.nwq.baseutils.R.color.red),
//                coordinate = viewModel.getCropArea()
//            )
//        )
//        findNavController().navigate(R.id.action_complexIndexFragment_to_nav_opt_preview)
    }
}