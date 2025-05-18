package com.example.myapplication.function

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.databinding.FragmentFunctionDetailBinding
import com.nwq.base.BaseToolBarFragment
import com.example.myapplication.R

class FunctionDetailFragment : BaseToolBarFragment<FragmentFunctionDetailBinding>() {

    private val args: FunctionDetailFragmentArgs by navArgs()
    private val viewModel: FunctionEdtViewModel by viewModels()


    override fun getLayoutId(): Int {
       return R.layout.fragment_function_detail
    }

    override fun getTitleRes(): Int {
       return R.string.function_detail
    }

    override fun onBackPress() {
        findNavController().popBackStack()
    }

    override fun initData() {
        super.initData()
        viewModel.initFunctionData(args.functionId)
    }



}