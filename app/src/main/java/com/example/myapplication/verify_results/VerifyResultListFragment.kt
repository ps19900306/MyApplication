package com.example.myapplication.verify_results


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.databinding.FragmentVerifyResultListBinding
import com.nwq.base.BaseFragment
import com.nwq.baseutils.runOnUI
import com.nwq.opencv.db.entity.TargetVerifyResult
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VerifyResultListFragment : BaseFragment<FragmentVerifyResultListBinding>() {

    private val args: VerifyResultListFragmentArgs by navArgs()
    private val viewModel: VerifyResultPViewModel by viewModels {
        AutoHsvRuleDetailViewModelFactory(args.FindTag)
    }


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVerifyResultListBinding {
        return FragmentVerifyResultListBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        super.initData()
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.resultFlow.collectLatest {
                    runOnUI {
                        initView(it)
                    }
                }
            }
        }

    }

    private fun initView(targetVerifyResults: List<TargetVerifyResult>) {
        binding.viewPager.adapter = VerifyResultAdapter(targetVerifyResults,requireActivity())
        binding.viewPager.currentItem= args.startIndex
        binding.viewPager.registerOnPageChangeCallback(object :
            androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.pageTv.text = "${position + 1}/${targetVerifyResults.size}"
            }
        })
    }

    inner class VerifyResultAdapter(
        val list: List<TargetVerifyResult>,
        fragmentActivity: FragmentActivity
    ) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            // 返回页面数量
            return list.size
        }

        override fun createFragment(position: Int): Fragment {
            // 返回每个位置对应的 Fragment
            return VerificationResultFragment(list.get(position).id)
        }
    }
}