package com.example.myapplication.verify_results


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Adapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.myapplication.adapter.TargetVerifyResultAdapter
import com.example.myapplication.databinding.FragmentVerifyResultIndexBinding
import com.nwq.adapter.KeyTextAdapter
import com.nwq.adapter.KeyTextCheckAdapter2
import com.nwq.base.BaseFragment
import com.nwq.baseutils.runOnUI
import com.nwq.callback.CallBack
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class VerifyResultIndexFragment : BaseFragment<FragmentVerifyResultIndexBinding>() {

    private val args: VerifyResultIndexFragmentArgs by navArgs()
    private val viewModel: VerifyResultPViewModel by viewModels {
        AutoHsvRuleDetailViewModelFactory(args.FindTag)
    }
    private lateinit var mAdapter: TargetVerifyResultAdapter
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVerifyResultIndexBinding {
        return FragmentVerifyResultIndexBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        super.initData()
        binding.isDoCb.isChecked = viewModel.isDo.value
        binding.isDoCb.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsDo(isChecked)
        }
        binding.isEffectiveCb.isChecked = viewModel.isEffective.value
        binding.isEffectiveCb.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsEffective(isChecked)
        }
        binding.isPassCb.isChecked = viewModel.isPass.value
        binding.isPassCb.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsPass(isChecked)
        }

        binding.typeRecyclerView.adapter = KeyTextCheckAdapter2(
            viewModel.typeList,
            object : CallBack<Int> {
                override fun onCallBack(data: Int) {
                    viewModel.setType(data)
                }
            }
        )
        mAdapter = TargetVerifyResultAdapter(object : CallBack<Int> {
            override fun onCallBack(data: Int) {

            }
        })
        binding.resultRecyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        binding.resultRecyclerView.adapter = mAdapter

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.resultFlow.collectLatest {
                    runOnUI {
                        mAdapter.updateData(it)
                    }
                }
            }
        }

    }
}