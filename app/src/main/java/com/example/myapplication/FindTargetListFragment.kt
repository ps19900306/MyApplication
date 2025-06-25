package com.example.myapplication


import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.base.NavigationContainerActivity2
import com.example.myapplication.databinding.FragmentSearchListBinding
import com.example.myapplication.find_target.FindTargetDetailFragmentArgs
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.baseutils.T
import com.nwq.callback.CallBack
import com.nwq.dialog.Simple2InputDialog
import com.nwq.dialog.SimpleInputDialog
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
                    Simple2InputDialog(titleRes = R.string.create_target) { name, description ->
                        lifecycleScope.launch {
                            if (TextUtils.isEmpty( name)|| TextUtils.isEmpty(description)){
                                T.show("请输入内容")
                                return@launch
                            }
                            val id = viewModel.createTarget(name, description)
                            NavigationContainerActivity2.startNavigationContainerActivity(
                                requireContext(),
                                R.navigation.nav_find_target,
                                FindTargetDetailFragmentArgs(id, name,description).toBundle()
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
        mCheckTextAdapter = CheckTextAdapter(mLongClick = object : CallBack<FindTargetRecord> {
            override fun onCallBack(data: FindTargetRecord) {
                NavigationContainerActivity2.startNavigationContainerActivity(
                    requireContext(),
                    R.navigation.nav_find_target,
                    FindTargetDetailFragmentArgs(data.id, data.keyTag,data.description).toBundle()
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
                        ICheckTextWrap<FindTargetRecord>(data) {
                            data.keyTag + "\n" + data.description
                        }
                    }
                    mCheckTextAdapter.upData(list)
                }
            }
        }
    }
}