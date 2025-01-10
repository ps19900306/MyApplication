package com.example.myapplication.verify_results



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.myapplication.databinding.FragmentVerificationResultBinding
import com.nwq.base.BaseFragment
import com.nwq.opencv.db.entity.TargetVerifyResult


class VerificationResultFragment(private val result: TargetVerifyResult,private val mViewModel:VerifyResultPViewModel) : BaseFragment<FragmentVerificationResultBinding>() {

    private val mVerifyResultPViewModel by viewModels<VerifyResultPViewModel>()
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVerificationResultBinding {
       return FragmentVerificationResultBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        super.initData()
        //binding.verticalViewPager.adapter = VerificationResultAdapter()
    }


}