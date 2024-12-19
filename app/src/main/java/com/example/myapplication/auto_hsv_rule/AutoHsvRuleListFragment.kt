package com.example.myapplication.auto_hsv_rule


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.myapplication.databinding.FragmentAutoHsvRuleListBinding
import com.nwq.base.BaseFragment

/**
 * A fragment representing a list of Items.
 */
class AutoHsvRuleListFragment : BaseFragment<FragmentAutoHsvRuleListBinding>() {

    private val viewModel by viewModels<AutoHsvRuleModel>({ requireActivity() })
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAutoHsvRuleListBinding {
        return FragmentAutoHsvRuleListBinding.inflate(inflater)
    }










}