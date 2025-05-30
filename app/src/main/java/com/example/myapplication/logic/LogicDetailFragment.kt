package com.example.myapplication.logic

import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.click.ClickSelectFragmentArgs
import com.example.myapplication.databinding.FragmentLogicDetailBinding
import com.nwq.base.BaseToolBarFragment
import com.nwq.baseutils.singleClick
import com.nwq.constant.ConstantKeyStr
import com.nwq.opencv.constant.LogicJudeResult
import com.nwq.opencv.db.entity.LogicEntity
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [LogicDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LogicDetailFragment : BaseToolBarFragment<FragmentLogicDetailBinding>() {

    private val args: LogicDetailFragmentArgs by navArgs()

    private val viewModel: LogicDetailViewModel by viewModels()
    private lateinit var mAddAdapter: CheckTextAdapter<LogicEntity>
    private lateinit var mDeleteAdapter: CheckTextAdapter<LogicEntity>

    private val SELECT_CLICK_TAG = "select_click"
    private val SELECT_FUNCTION_TAG = "select_function"
    private val ADD_LOGIC_TAG = "add_logic"
    private val DELETE_LOGIC_TAG = "delete_logic"
    private val adapter by lazy {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            viewModel.items
        )
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_logic_detail
    }

    override fun getTitleRes(): Int {
        return R.layout.fragment_function_list
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_logic_detail
    }

    override fun onMenuItemClick(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.action_add -> {
                findNavController().navigate(
                    R.id.action_logicDetailFragment_to_LogicSelectFragment,
                    ClickSelectFragmentArgs(ADD_LOGIC_TAG).toBundle()
                )
            }

            R.id.action_delete -> {
                findNavController().navigate(
                    R.id.action_logicDetailFragment_to_LogicSelectFragment,
                    ClickSelectFragmentArgs(DELETE_LOGIC_TAG).toBundle()
                )
            }

            R.id.action_delete_select -> {
                viewModel.mAddLogicListFow.tryEmit(mAddAdapter.removeSelectAndGet().toMutableList())
                viewModel.mDeleteLogicListFow.tryEmit(
                    mDeleteAdapter.removeSelectAndGet().toMutableList()
                )
            }
            R.id.action_save -> {
                viewModel.saveAll()
                findNavController().popBackStack()
            }
        }
    }

    override fun onBackPress() {
        findNavController().popBackStack()
    }

    override fun initView() {
        super.initView()
        LogicJudeResult
        // 绑定 Spinner 到适配器
        binding.resultSpinner.adapter = adapter

        // 设置点击监听器
        binding.resultSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                showUiGroup(viewModel.setResultSelection(position))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //选择点击区域
        binding.clickEntityTv.singleClick {
            findNavController().navigate(
                R.id.action_logicDetailFragment_to_ClickSelectFragment,
                ClickSelectFragmentArgs(DELETE_LOGIC_TAG).toBundle()
            )
        }
        parentFragment?.setFragmentResultListener(
            SELECT_CLICK_TAG, // 这个 tag 要和 ClickSelectFragment 接收到的 args.actionTag 一致
            { requestKey, result ->
                val selectedIds = result.getLongArray(ConstantKeyStr.SELECTED_RESULT)
                selectedIds?.get(0)?.let { viewModel.updateClickEntity(it) }
            })

        //选择功能区域
        binding.functionEntityTv.singleClick {
            findNavController().navigate(
                R.id.action_logicDetailFragment_to_FunctionSelectFragment,
                ClickSelectFragmentArgs(SELECT_FUNCTION_TAG).toBundle()
            )
        }
        parentFragment?.setFragmentResultListener(
            SELECT_FUNCTION_TAG, // 这个 tag 要和 ClickSelectFragment 接收到的 args.actionTag 一致
            { requestKey, result ->
                val selectedIds = result.getLongArray(ConstantKeyStr.SELECTED_RESULT)
                selectedIds?.get(0)?.let { viewModel.updatesStartFunction(it) }
            })


        //设置新增和去除的逻辑
        binding.addRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mAddAdapter = CheckTextAdapter()
        binding.addRecyclerView.adapter = mAddAdapter

        binding.deleteRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mDeleteAdapter = CheckTextAdapter()
        binding.deleteRecyclerView.adapter = mDeleteAdapter

        parentFragment?.setFragmentResultListener(
            ADD_LOGIC_TAG, // 这个 tag 要和 ClickSelectFragment 接收到的 args.actionTag 一致
            { requestKey, result ->
                val selectedIds = result.getLongArray(ConstantKeyStr.SELECTED_RESULT)
                viewModel.addAddLogic(selectedIds)
            })

        parentFragment?.setFragmentResultListener(
            DELETE_LOGIC_TAG, // 这个 tag 要和 ClickSelectFragment 接收到的 args.actionTag 一致
            { requestKey, result ->
                val selectedIds = result.getLongArray(ConstantKeyStr.SELECTED_RESULT)
                viewModel.addClearLogic(selectedIds)
            })

        binding.consecutiveEntriesEdt.addTextChangedListener {
            try {
                viewModel.updateConsecutiveEntries(it.toString().toInt())
            } catch (e: Exception) {
                binding.consecutiveEntriesEdt.setText("-1")
            }
        }
    }


    override fun initData() {
        super.initData()
        lifecycleScope.launch {
            val data = viewModel.initLogicEntity(args.logicId)
            data?.let { initLogicEntity(it) }
        }
        //  监听点击区域参数设置
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED, {
                viewModel.mClickEntityFlow.collectLatest {
                    if (it != null) {
                        binding.clickEntityTv.setText(it.keyTag)
                    }
                }
            })
        }
        //  添加新的逻辑
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED, {
                viewModel.mAddLogicListFow.collectLatest {
                    if (it != null) {
                        mAddAdapter.upData(it.map { data ->
                            ICheckTextWrap<LogicEntity>(data) {
                                data.keyTag
                            }
                        })
                    }
                }
            })
        }
        //  减少的逻辑新的逻辑
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED, {
                viewModel.mDeleteLogicListFow.collectLatest {
                    if (it != null) {
                        mDeleteAdapter.upData(it.map { data ->
                            ICheckTextWrap<LogicEntity>(data) {
                                data.keyTag
                            }
                        })
                    }
                }
            })
        }
    }


    private fun initLogicEntity(data: LogicEntity) {
        binding.resultSpinner.setSelection(viewModel.getResultSelection())
        setTitleString(data.keyTag)
        binding.consecutiveEntriesEdt.setText(data.consecutiveEntries.toString())
    }

    //需要根据类型显示不同UI
    private fun showUiGroup(isFunction: Boolean) {
        binding.functionGroup.visibility = if (isFunction) View.VISIBLE else View.GONE
        binding.normalGroup.visibility = if (isFunction) View.GONE else View.VISIBLE
    }
}