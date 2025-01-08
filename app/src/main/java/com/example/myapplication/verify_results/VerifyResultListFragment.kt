package com.example.myapplication.verify_results


import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.databinding.FragmentVerifyResultListBinding
import com.nwq.base.BaseFragment

class VerifyResultListFragment : BaseFragment<FragmentVerifyResultListBinding>() {


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVerifyResultListBinding {
        return FragmentVerifyResultListBinding.inflate(inflater, container, false)
    }


}