package com.example.myapplication.click

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.base.AppToolBarFragment
import com.example.myapplication.base.TouchOptModel
import com.example.myapplication.databinding.FragmentClickDetailBinding
import com.example.myapplication.find_target.TargetSelectFragmentArgs
import com.example.myapplication.preview.PreviewOptItem
import com.example.myapplication.preview.PreviewViewModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseobj.PreviewCoordinateData
import com.nwq.baseutils.MatUtils
import com.nwq.constant.ConstantKeyStr
import com.nwq.loguitls.L
import com.nwq.opencv.db.entity.ClickEntity
import kotlinx.coroutines.launch

/**
 * [ClickEntity]
 */
class ClickDetailFragment : AppToolBarFragment<FragmentClickDetailBinding>() {
    override fun createBinding(inflater: LayoutInflater): FragmentClickDetailBinding {
        return FragmentClickDetailBinding.inflate(inflater)
    }

    private val args: ClickDetailFragmentArgs by navArgs()

    private val TAG = "ClickDetailFragment"
    private val viewModel: ClickDetailViewModel by viewModels({ requireActivity() })
    private val preViewModel: PreviewViewModel by viewModels({ requireActivity() })
    private val SELECT_TARGET_TAG = "select_target"
    override fun getMenuRes(): Int {
        return R.menu.menu_click_detail
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {

        when (menuItem.itemId) {
            R.id.action_set_check_target -> {
                findNavController().navigate(
                    R.id.action_ClickDetailFragment_to_nav_target_select,
                    TargetSelectFragmentArgs(SELECT_TARGET_TAG).toBundle()
                )
            }

            R.id.action_select_picture -> {
                selectPicture()
            }

            R.id.action_set_click_area -> {
                setClickArea()
            }

            R.id.action_switch_round -> {
                viewModel.mClickEntity?.isRound = !viewModel.mClickEntity?.isRound!!
                setInfo()
            }

            R.id.action_save -> {
                viewModel.save()
            }
        }
        return true
    }

    override fun onBackPress(): Boolean {
        return false
    }


    override fun initData() {
        super.initData()
        parentFragment?.setFragmentResultListener(
            SELECT_TARGET_TAG, // 这个 tag 要和 ClickSelectFragment 接收到的 args.actionTag 一致
            { requestKey, result ->
                val selectedIds = result.getLongArray(ConstantKeyStr.SELECTED_RESULT)
                selectedIds?.get(0)?.let {
                    viewModel.updateFindTarget(it)
                }
            })
        lifecycleScope.launch {
            viewModel.init(args.clickId)
            setInfo()
        }

    }

    override fun onResume() {
        super.onResume()
        preViewModel.optList.find { it.key ==  com.nwq.baseutils.R.string.click_area }?.coordinate?.let {
            if (it is CoordinateArea) {
                viewModel.clickArea = it
                setInfo()
            }
        }
    }


    private fun setInfo() {
        viewModel.mClickEntity?.let {
            binding.infoTv.text =
                it.toStringSimple() + "\n" + "新设点击区域：" + viewModel.clickArea?.toStringSimple()
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
                            viewModel.mSrcBitmap = it
                            preViewModel.mBitmap = it
                            viewModel.path = localMedia.realPath
                            viewModel.storageType = MatUtils.REAL_PATH_TYPE
                        }
                    }
                }

                override fun onCancel() {
                    L.i(TAG, "onCancel")
                }
            })
    }


    private fun setClickArea() {
        if (preViewModel.optList.isEmpty()) {
            preViewModel.optList.add(
                PreviewOptItem(
                    key =  com.nwq.baseutils.R.string.click_area,
                    type = TouchOptModel.RECT_AREA_TYPE,
                    color = ContextCompat.getColor(requireContext(), com.nwq.baseutils.R.color.red),
                    coordinate = viewModel.clickArea
                )
            )
        }
        preViewModel.defaultAreaList.clear()
        viewModel.findArea?.let {
            preViewModel.defaultAreaList.add(
                PreviewCoordinateData(
                    it,
                    com.nwq.baseutils.R.color.button_normal,
                    1f
                )
            )
        }
        viewModel.targetOriginalArea?.let {
            preViewModel.defaultAreaList.add(
                PreviewCoordinateData(
                    it,
                    com.nwq.baseutils.R.color.button_normal,
                    1f
                )
            )
        }
        preViewModel.mBitmap = viewModel.mSrcBitmap
        findNavController().navigate(R.id.action_ClickDetailFragment_to_nav_opt_preview)
    }
}