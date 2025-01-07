package com.example.myapplication.verify_results


import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.databinding.FragmentVerifyPointInfoBinding
import com.nwq.base.BaseFragment




class VerifyPointInfoFragment : BaseFragment<FragmentVerifyPointInfoBinding>() {


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVerifyPointInfoBinding {
        return  FragmentVerifyPointInfoBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        super.initData()
    }
}