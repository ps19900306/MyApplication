package com.example.myapplication.auto_hsv_rule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.myapplication.databinding.FragmentAutoHsvRuleDetailBinding
import com.nwq.base.BaseFragment
import com.nwq.opencv.db.entity.AutoRulePointEntity
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [AutoHsvRuleDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AutoHsvRuleDetailFragment : BaseFragment<FragmentAutoHsvRuleDetailBinding>() {

    private val args: AutoHsvRuleDetailFragmentArgs by navArgs()
    private val viewModel by viewModels<AutoHsvRuleModel>({ requireActivity() })
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAutoHsvRuleDetailBinding {
        return FragmentAutoHsvRuleDetailBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        super.initData()
//        lifecycleScope.launch {
//            lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED){
//                viewModel.getByTagFlow(args.autoHsvRuleTag)?.collect{
//                    initView(it)
//                }
//            }
//        }
    }

    private fun initView(autoRulePointEntity: AutoRulePointEntity) {

    }
}