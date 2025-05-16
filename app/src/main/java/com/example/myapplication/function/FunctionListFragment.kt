package com.example.myapplication.function


import android.view.MenuItem
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentFunctionListBinding
import com.nwq.base.BaseToolBarFragment
import com.example.myapplication.R
import com.nwq.opencv.db.entity.FunctionEntity
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.launch

class FunctionListFragment : BaseToolBarFragment<FragmentFunctionListBinding>() {

    private val viewModel: FunctionViewModel by viewModels()

    private lateinit var mCheckTextAdapter: CheckTextAdapter<FunctionEntity>


    override fun getLayoutId(): Int {
        return R.layout.fragment_function_list
    }

    override fun getTitleRes(): Int {
        return R.string.function_list
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_list_edit
    }


    override fun onMenuItemClick(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.action_add -> {
                val dialog = EditFunctionTitleDialog { name, description ->
                    lifecycleScope.launch {
                        val id = viewModel.createFunction(name, description)
                        findNavController().navigate(
                            R.id.action_functionListFragment_to_functionDetailFragment,
                            FunctionDetailFragmentArgs(id).toBundle()
                        )
                    }
                }
                dialog.show(parentFragmentManager, "EditFunctionTitleDialog")
            }

            R.id.action_delete_select -> {
                viewModel.delete(mCheckTextAdapter.getSelectedItem().map { it.getT() })
            }

            R.id.action_delete_all -> {

            }
        }
    }


    override fun onBackPress() {
        requireActivity().finish()
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