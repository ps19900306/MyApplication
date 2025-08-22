package com.example.myapplication.find_target


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat


import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.databinding.FragmentFindTargetDetailBinding
import com.example.myapplication.R
import com.example.myapplication.base.TouchOptModel
import com.example.myapplication.preview.PreviewOptItem
import com.example.myapplication.preview.PreviewViewModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.baseobj.CoordinateArea
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.singleClick
import com.nwq.loguitls.L
import kotlinx.coroutines.launch


/**
 * [com.nwq.opencv.db.entity.FindTargetRecord]
 */
class FindTargetDetailFragment : BaseToolBar2Fragment<FragmentFindTargetDetailBinding>() {

    private val TAG = "FindTargetDetailFragment"
    private val args: FindTargetDetailFragmentArgs by navArgs()
    private val viewModel: FindTargetDetailModel by viewModels({ requireActivity() })
    private val preViewModel: PreviewViewModel by viewModels({ requireActivity() })
    override fun createBinding(inflater: LayoutInflater): FragmentFindTargetDetailBinding {
        return FragmentFindTargetDetailBinding.inflate(inflater)
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_target_record
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        var flag = true
        when (menuItem.itemId) {
            R.id.action_select_picture -> {
                selectPicture()
            }

            R.id.action_save -> {
                viewModel.save()
            }

            R.id.action_area -> {
                findArea()
            }

            R.id.action_clear -> {
                viewModel.clear()
            }

            else -> {
                flag = false
            }
        }
        return flag
    }

    override fun onResume() {
        super.onResume()
        preViewModel.optList.find { it.key == com.nwq.baseutils.R.string.select_critical_area }?.coordinate?.let {
            if (it is CoordinateArea)
                viewModel.targetOriginalArea = it
        }
        preViewModel.optList.find { it.key == com.nwq.baseutils.R.string.find_the_image_area }?.coordinate?.let {
            if (it is CoordinateArea)
                viewModel.findArea = it
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


    override fun onBackPress(): Boolean {
        requireActivity().finish()
        return true
    }

    override fun initData() {
        super.initData()

        viewModel.init(args.targetId);
        binding.hsvBgView.singleClick {
            findNavController().navigate(
                R.id.action_findTargetDetailFragment_to_hsvTargetDetailFragment,
                HsvTargetDetailFragmentArgs(args.bigTitle).toBundle()
            )
        }
        binding.rgbBgView.singleClick {
            findNavController().navigate(
                R.id.action_findTargetDetailFragment_to_rgbTargetDetailFragment,
                RgbTargetDetailFragmentArgs(args.bigTitle).toBundle()
            )
        }
        binding.imgBgView.singleClick {
            findNavController().navigate(
                R.id.action_findTargetDetailFragment_to_imgTargetDetailFragment,
                ImgTargetDetailFragmentArgs(args.bigTitle).toBundle()
            )
        }
        binding.matBgView.singleClick {
            findNavController().navigate(
                R.id.action_findTargetDetailFragment_to_MatTargetDetailFragment,
                MatTargetDetailFragmentArgs(args.bigTitle).toBundle()
            )
        }
    }



    //选择图片和关键区域
    private fun findArea() {
        if (preViewModel.optList.isEmpty()) {
            preViewModel.optList.add(
                PreviewOptItem(
                    key = com.nwq.baseutils.R.string.select_critical_area,
                    type = TouchOptModel.RECT_AREA_TYPE,
                    color = ContextCompat.getColor(requireContext(), com.nwq.baseutils.R.color.red),
                    coordinate = viewModel.targetOriginalArea
                )
            )
            preViewModel.optList.add(
                PreviewOptItem(
                    key = com.nwq.baseutils.R.string.find_the_image_area,
                    type = TouchOptModel.RECT_AREA_TYPE,
                    color = ContextCompat.getColor(
                        requireContext(),
                        com.nwq.baseutils.R.color.button_normal
                    ),
                    coordinate = viewModel.findArea
                )
            )
        }
        preViewModel.mBitmap = viewModel.mSrcBitmap
        findNavController().navigate(R.id.action_findTargetDetailFragment_to_nav_opt_preview)
    }


}





