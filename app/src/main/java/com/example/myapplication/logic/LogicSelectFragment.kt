package com.example.myapplication.logic

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
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentFunctionListBinding
import com.nwq.base.BaseToolBarFragment
import com.nwq.constant.ConstantKeyStr
import com.nwq.opencv.db.entity.LogicEntity
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.launch


class LogicSelectFragment : BaseToolBarFragment<FragmentFunctionListBinding>() {

    private val args: LogicSelectFragmentArgs by navArgs()



    private val viewModel: LogicSelectViewModel by viewModels()

    private lateinit var mCheckTextAdapter: CheckTextAdapter<LogicEntity>
    override fun getLayoutId(): Int {
        return R.layout.`fragment_search_list.xml`
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
            putLongArray(ConstantKeyStr.SELECTED_RESULT, selectedItems.map { it.getT().id }.toLongArray())
        }
        parentFragmentManager.setFragmentResult(args.actionTag, result)
        findNavController().popBackStack()
    }

    override fun initView() {
        viewModel.id = args.functionId
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
                        ICheckTextWrap<LogicEntity>(data) {
                            data.keyTag
                        }
                    }
                    mCheckTextAdapter.upData(list)
                }
            }
        }
    }


}