package com.example.myapplication.logic

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLogicDetailBinding
import com.example.myapplication.function.FunctionDetailFragmentArgs
import com.example.myapplication.function.FunctionEdtViewModel
import com.nwq.base.BaseToolBarFragment
import com.nwq.opencv.constant.LogicJudeResult

/**
 * A simple [Fragment] subclass.
 * Use the [LogicDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LogicDetailFragment : BaseToolBarFragment<FragmentLogicDetailBinding>() {

    private val args: LogicDetailFragmentArgs by navArgs()

    private val viewModel: FunctionEdtViewModel by viewModels({ requireActivity() })

    override fun getLayoutId(): Int {
         return R.layout.fragment_logic_detail
    }

    override fun getTitleRes(): Int {
        return R.layout.fragment_function_list
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_logic_detail
    }

    override fun onBackPress() {
        findNavController().popBackStack()
    }

    override fun initView() {
        super.initView()
        LogicJudeResult
        binding.resultSpinner
    }

    override fun initData() {
        super.initData()
    }



}