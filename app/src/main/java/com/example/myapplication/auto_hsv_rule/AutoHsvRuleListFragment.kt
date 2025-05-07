package com.example.myapplication.auto_hsv_rule


import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.auto_hsv_rule.adapter.AutoHsvRuleAdapter
import com.example.myapplication.databinding.FragmentAutoHsvRuleListBinding
import com.nwq.base.BaseFragment
import com.nwq.baseutils.runOnUI
import com.nwq.baseutils.singleClick
import com.nwq.callback.CallBack
import com.nwq.opencv.IAutoRulePoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class AutoHsvRuleListFragment : BaseFragment<FragmentAutoHsvRuleListBinding>() {

    private val viewModel by viewModels<AutoHsvRuleModel>({ requireActivity() })
    private lateinit var mAdapter: AutoHsvRuleAdapter
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAutoHsvRuleListBinding {
        return FragmentAutoHsvRuleListBinding.inflate(inflater)
    }

    override fun initData() {
        super.initData()

        mAdapter = AutoHsvRuleAdapter(true)
        binding.recyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = mAdapter
        binding.searchTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                viewModel.updateSearchStr(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resultsFlow.collectLatest {
                    runOnUI {
                        mAdapter.updateData(it)
                    }
                }
            }
        }
        mAdapter.setItemClickListener(object : CallBack<IAutoRulePoint?> {
            override fun onCallBack(data: IAutoRulePoint?) {
                data?.getTag()?.let { t->
                    findNavController().navigate(R.id.action_autoHsvRuleListFragment_to_autoHsvRuleDetailFragment,  AutoHsvRuleDetailFragmentArgs(t).toBundle())
                }
            }
        })

        binding.createBtn.singleClick {
            findNavController().navigate(R.id.action_autoHsvRuleListFragment_to_autoHsvRuleDetailFragment,  AutoHsvRuleDetailFragmentArgs(null).toBundle())
        }
    }

}