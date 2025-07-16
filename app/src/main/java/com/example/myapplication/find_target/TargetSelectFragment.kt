package com.example.myapplication.find_target

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSearchListBinding
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.constant.ConstantKeyStr
import com.nwq.opencv.db.entity.FindTargetRecord
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.launch

/**
 * [com.nwq.opencv.db.entity.FindTargetRecord]
 */
class TargetSelectFragment : BaseToolBar2Fragment<FragmentSearchListBinding>() {

    private val args: TargetSelectFragmentArgs by navArgs()

    private val viewModel: TargetSelectViewModel by viewModels()

    private lateinit var mCheckTextAdapter: CheckTextAdapter<FindTargetRecord>

    override fun createBinding(inflater: LayoutInflater): FragmentSearchListBinding {
        return FragmentSearchListBinding.inflate(inflater)
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_list_select
    }


    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
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
        return true
    }


    override fun onBackPress(): Boolean {
        val selectedItems = mCheckTextAdapter.getSelectedItem()
        val result = Bundle().apply {
            putLongArray(
                ConstantKeyStr.SELECTED_RESULT,
                selectedItems.map { it.getT().id }.toLongArray()
            )
        }
        parentFragment?.setFragmentResult(args.actionTag, result)
        findNavController().popBackStack()
        return true
    }

    override fun initView() {
        mCheckTextAdapter = CheckTextAdapter()
        binding.recycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recycler.adapter = mCheckTextAdapter
        binding.inputEdit.addTextChangedListener {
            val text = it?.toString() ?: ""
            viewModel.updateLogicSearchStr(text)
        }
    }

    override fun initData() {
        super.initData()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.logicSearchFlow.collect {
                    val list = it.map { data ->
                        ICheckTextWrap<FindTargetRecord>(data) {
                            data.keyTag
                        }
                    }
                    mCheckTextAdapter.upData(list)
                }
            }
        }
    }


}