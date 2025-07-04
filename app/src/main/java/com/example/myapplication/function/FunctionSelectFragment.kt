package com.example.myapplication.function


import android.os.Bundle
import android.view.MenuItem
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.FunctionViewModel
import com.nwq.base.BaseToolBarFragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSearchListBinding
import com.nwq.constant.ConstantKeyStr
import com.nwq.opencv.db.entity.FunctionEntity
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.launch

class FunctionSelectFragment : BaseToolBarFragment<FragmentSearchListBinding>() {

    private val viewModel: FunctionViewModel by viewModels()
    private val args: FunctionSelectFragmentArgs by navArgs()
    private lateinit var mCheckTextAdapter: CheckTextAdapter<FunctionEntity>

    override fun getLayoutId(): Int {
        return R.layout.fragment_search_list
    }

    override fun getTitleRes(): Int {
        return R.string.please_select
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_list_select
    }


    override fun onMenuItemClick(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.action_select_all -> {
                mCheckTextAdapter.selectAll(true)
            }

            R.id.action_delete_all -> {
                mCheckTextAdapter.selectAll(false)
            }

            R.id.action_reverse_all -> {
                mCheckTextAdapter.selectReverse()
            }
        }
    }


    override fun onBackPress() {
        val selectedItems = mCheckTextAdapter.getSelectedItem()
        val result = Bundle().apply {
            putLongArray(
                ConstantKeyStr.SELECTED_RESULT,
                selectedItems.map { it.getT().id }.toLongArray()
            )
        }
        parentFragmentManager.setFragmentResult(args.actionTag, result)
        findNavController().popBackStack()
    }


    override fun initView() {
        super.initView()
        mCheckTextAdapter = CheckTextAdapter()
        binding.recycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recycler.adapter = mCheckTextAdapter
        binding.inputEdit.addTextChangedListener {
            val text = it?.toString() ?: ""
            viewModel.updateSearchStr(text)
        }
    }


    override fun initData() {
        super.initData()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.resultsFlow.collect {
                    val list = it.map { data ->
                        ICheckTextWrap<FunctionEntity>(data) {
                            data.keyTag
                        }
                    }
                    mCheckTextAdapter.upData(list)
                }
            }
        }
    }
}