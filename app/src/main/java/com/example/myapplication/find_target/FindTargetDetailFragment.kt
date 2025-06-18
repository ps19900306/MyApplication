package com.example.myapplication.find_target


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
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.singleClick
import kotlinx.coroutines.launch


/**
 * [com.nwq.opencv.db.entity.FindTargetRecord]
 */
class FindTargetDetailFragment : BaseToolBar2Fragment<FragmentFindTargetDetailBinding>() {

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

            R.id.action_save -> {
                save()
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

    override fun onBackPress(): Boolean {
        return false
    }

    override fun initData() {
        super.initData()
        lifecycleScope.launch {
            viewModel.init(args.targetId)
        }
//        binding.hsvBgView.singleClick {
//            findNavController().navigate(R.id.action_findTargetDetailFragment_to_hsvTargetDetailFragment,HsvTargetDetailFragmentArgs(args.@string/bigTitleKey))
//        }
//        binding.rgbBgView.singleClick {
//            findNavController().navigate(R.id.action_findTargetDetailFragment_to_nav_opt_preview)
//        }
//        binding.imgBgView.singleClick {
//            findNavController().navigate(R.id.action_findTargetDetailFragment_to_nav_opt_preview)
//        }
    }

    private fun save() {
        viewModel.save(preViewModel.path,preViewModel.type,preViewModel.getCoordinate(key = R.string.select_critical_area),preViewModel.getCoordinate(key = R.string.find_the_image_area))

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
        preViewModel.optList.add(
            PreviewOptItem(
                key = R.string.find_the_image_area,
                type = TouchOptModel.RECT_AREA_TYPE,
                color = ContextCompat.getColor(
                    requireContext(),
                    com.nwq.baseutils.R.color.button_normal
                )
            )
        )
        preViewModel.path = viewModel.path
        preViewModel.type = viewModel.storageType

        findNavController().navigate(R.id.action_findTargetDetailFragment_to_nav_opt_preview)
    }


}





