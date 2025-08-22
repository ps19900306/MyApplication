package com.example.myapplication


import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.base.NavigationToolBarActivity
import com.example.myapplication.databinding.FragmentSearchListBinding
import com.example.myapplication.function.FunctionDetailFragmentArgs
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.callback.CallBack
import com.nwq.dialog.Simple2InputDialog
import com.nwq.opencv.db.entity.FunctionEntity
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.launch

class FunctionListFragment : BaseToolBar2Fragment<FragmentSearchListBinding>() {

    private val viewModel: FunctionViewModel by viewModels()
    private lateinit var mCheckTextAdapter: CheckTextAdapter<FunctionEntity>

    override fun createBinding(inflater: LayoutInflater): FragmentSearchListBinding {
        return FragmentSearchListBinding.inflate(inflater)
    }


    override fun getMenuRes(): Int {
        return R.menu.menu_list_edit
    }


    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_add -> {
                val dialog =
                    Simple2InputDialog(titleRes = com.nwq.baseutils.R.string.create_function) { name, description ->
                        lifecycleScope.launch {
                            val id = viewModel.createFunction(name, description)
                            NavigationToolBarActivity.startNavigationContainerActivity(
                                requireContext(),
                                R.navigation.nav_function,
                                FunctionDetailFragmentArgs(id, name, description).toBundle()
                            )
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


    override fun onBackPress(): Boolean {
        requireActivity().finish()
        return true;
    }


    override fun initView() {
        super.initView()
        mCheckTextAdapter = CheckTextAdapter(mLongClick = object : CallBack<FunctionEntity> {
            override fun onCallBack(data: FunctionEntity) {
                NavigationToolBarActivity.startNavigationContainerActivity(
                    requireContext(),
                    R.navigation.nav_function,
                    FunctionDetailFragmentArgs(data.id, data.keyTag, data.description).toBundle()
                )
            }

        })
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