package com.example.myapplication.function


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.myapplication.databinding.FragmentFunctionDetailBinding
import com.nwq.base.BaseFragment



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
        viewModel.initFunctionData(args.functionId)
    }



}