package com.example.myapplication



import android.view.MenuItem
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentSearchListBinding
import com.example.myapplication.function.EditFunctionTitleDialog
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.opencv.db.entity.FindTargetRecord
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class FindTargetListFragment : BaseToolBar2Fragment<FragmentSearchListBinding>() {

    private val viewModel: FindTargetModel by viewModels()
    private lateinit var mCheckTextAdapter: CheckTextAdapter<FindTargetRecord>

    override fun getLayoutId(): Int {
        return R.layout.fragment_search_list
    }


    override fun getMenuRes(): Int {
        return R.menu.menu_list_edit
    }


    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_add -> {
                val dialog = EditFunctionTitleDialog { name, description ->
                    lifecycleScope.launch {
                        val id = viewModel.createTarget(name, description)
//                        findNavController().navigate(
//                            R.id.action_functionListFragment_to_functionDetailFragment,
//                            FunctionDetailFragmentArgs(id).toBundle()
//                        )
                    }
                }
                dialog.show(parentFragmentManager, "EditFunctionTitleDialog")
                return true
            }

            R.id.action_delete_select -> {
                viewModel.delete(mCheckTextAdapter.getSelectedItem().map { it.getT() })
                return true
            }

            R.id.action_delete_all -> {
                showTipsDialog() { b ->
                    if (b) {
                        viewModel.deleteAll()
                    }
                }
                return true
            }
        }
        return false
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