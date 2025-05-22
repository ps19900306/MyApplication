package com.example.myapplication.logic

import android.view.MenuItem
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentFunctionListBinding
import com.example.myapplication.function.FunctionEdtViewModel
import com.nwq.base.BaseToolBarFragment
import com.nwq.opencv.db.entity.LogicEntity
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.launch


class LogicSelectFragment : BaseToolBarFragment<FragmentFunctionListBinding>() {


    private val viewModel: FunctionEdtViewModel by viewModels({ requireActivity() })

    private lateinit var mCheckTextAdapter: CheckTextAdapter<LogicEntity>
    override fun getLayoutId(): Int {
        return R.layout.fragment_function_list
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
        findNavController().popBackStack()
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