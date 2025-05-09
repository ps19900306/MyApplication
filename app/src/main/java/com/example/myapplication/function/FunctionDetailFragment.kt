package com.example.myapplication.function

import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.myapplication.databinding.FragmentFunctionDetailBinding
import com.example.myapplication.verify_results.AutoHsvRuleDetailViewModelFactory
import com.example.myapplication.verify_results.VerifyResultIndexFragmentArgs
import com.example.myapplication.verify_results.VerifyResultPViewModel
import com.nwq.base.BaseFragment
import com.nwq.loguitls.L
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FunctionDetailFragment : BaseFragment<FragmentFunctionDetailBinding>() {

    private val args: FunctionDetailFragmentArgs by navArgs()
    private val viewModel: FunctionEdtViewModel by viewModels()
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFunctionDetailBinding {
        return FragmentFunctionDetailBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        super.initData()

        repeatOnLifecycle(Lifecycle.State.STARTED,)
        viewModel.initFunctionData(args.functionId)
    }



}