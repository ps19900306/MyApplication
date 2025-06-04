package com.example.myapplication


import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.auto_hsv_rule.AutoHsvRuleModel
import com.example.myapplication.auto_hsv_rule.adapter.AutoHsvRuleAdapter
import com.example.myapplication.databinding.FragmentAutoHsvRuleListBinding
import com.example.myapplication.databinding.FragmentSearchListBinding
import com.nwq.base.BaseFragment
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.baseutils.runOnUI
import com.nwq.baseutils.singleClick
import com.nwq.callback.CallBack
import com.nwq.dialog.SimpleInputDialog
import com.nwq.opencv.IAutoRulePoint
import com.nwq.opencv.db.entity.FindTargetRecord
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class AutoHsvRuleListFragment : BaseToolBar2Fragment<FragmentSearchListBinding>() {

    private val viewModel: FindTargetModel by viewModels()
    private lateinit var mCheckTextAdapter: CheckTextAdapter<FindTargetRecord>



    override fun createBinding(inflater: LayoutInflater): FragmentSearchListBinding {
        return FragmentSearchListBinding.inflate(inflater)
    }


    override fun getMenuRes(): Int {
        return R.menu.menu_list_edit
    }


    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_add -> {
                val dialog = SimpleInputDialog(titleRes= R.string.create_target) { name, description ->
                    lifecycleScope.launch {
                        val id = viewModel.createTarget(name, description)
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