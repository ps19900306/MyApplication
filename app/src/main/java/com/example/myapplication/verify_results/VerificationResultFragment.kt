package com.example.myapplication.verify_results



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.databinding.FragmentVerificationResultBinding
import com.nwq.base.BaseFragment




class VerificationResultFragment(id:Long) : BaseFragment<FragmentVerificationResultBinding>() {

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


//    private class VerificationResultAdapter(
//        private val fragmentList: List<Fragment>,
//        fragmentManager: FragmentManager,
//        lifecycle: androidx.lifecycle.Lifecycle
//    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
//
//        override fun getItemCount(): Int = fragmentList.size
//
//        override fun createFragment(position: Int): Fragment = fragmentList[position]
//    }
}