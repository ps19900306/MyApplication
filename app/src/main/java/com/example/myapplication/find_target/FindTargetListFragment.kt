package com.example.myapplication.find_target


import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myapplication.adapter.FindTargetListAdapter
import com.example.myapplication.databinding.FragmentFindTargetListBinding
import com.nwq.base.BaseFragment
import com.nwq.baseutils.runOnUI
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class FindTargetListFragment : BaseFragment<FragmentFindTargetListBinding>() {

    private val viewModel by viewModels<FindTargetModel>({ requireActivity() })
    private lateinit var mAdapter: FindTargetListAdapter
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFindTargetListBinding {
        return FragmentFindTargetListBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        mAdapter = FindTargetListAdapter()
        binding.recyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = FindTargetListAdapter()

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

    }


}