package com.example.myapplication.verify_results


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.myapplication.databinding.FragmentVerifyResultIndexBinding
import com.nwq.adapter.KeyTextAdapter
import com.nwq.base.BaseFragment
import com.nwq.callback.CallBack


class VerifyResultIndexFragment : BaseFragment<FragmentVerifyResultIndexBinding>() {

    private val args: VerifyResultIndexFragmentArgs by navArgs()
    private val viewModel: VerifyResultPViewModel by viewModels {
        AutoHsvRuleDetailViewModelFactory(args.FindTag)
    }
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVerifyResultIndexBinding {
      return  FragmentVerifyResultIndexBinding.inflate(inflater, container, false)
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

        binding.typeRecyclerView.adapter = KeyTextAdapter(
            viewModel.typeList,
            object : CallBack<Int> {
                override fun onCallBack(data: Int) {
                    viewModel.setType(data)
                }
            }
       )
       binding.resultRecyclerView

    }
}