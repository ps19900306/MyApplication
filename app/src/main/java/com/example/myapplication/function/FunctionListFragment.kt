package com.example.myapplication.function


import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.databinding.FragmentFunctionListBinding
import com.nwq.base.BaseFragment



class FunctionListFragment : BaseFragment<FragmentFunctionListBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFunctionListBinding {
       return FragmentFunctionListBinding.inflate(inflater, container, false)
    }


}