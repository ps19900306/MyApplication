package com.example.myapplication.find_target


import android.view.LayoutInflater
import android.view.MenuItem


import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.myapplication.databinding.FragmentFindTargetDetailBinding
import com.example.myapplication.R
import com.example.myapplication.preview.PreviewViewModel
import com.nwq.base.BaseToolBar2Fragment
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
            R.id.action_delete_select -> {

            }

            R.id.action_save -> {

            }

            R.id.action_add_img -> {

            }

            R.id.action_add_hsv -> {

            }

            R.id.action_add_rgb -> {

            }

            R.id.action_add_mat -> {

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
    }



}





