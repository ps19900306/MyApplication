package com.example.myapplication.function

import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentFunctionDetailBinding
import com.nwq.base.BaseToolBarFragment
import com.example.myapplication.R
import com.nwq.callback.CallBack
import com.nwq.opencv.db.entity.FunctionEntity
import com.nwq.opencv.db.entity.LogicEntity
import com.nwq.simplelist.ICheckTextWrap
import com.nwq.simplelist.ITextWarp
import com.nwq.simplelist.TextAdapter
import kotlinx.coroutines.launch

class FunctionDetailFragment : BaseToolBarFragment<FragmentFunctionDetailBinding>() {

    private val args: FunctionDetailFragmentArgs by navArgs()
    private val viewModel: FunctionEdtViewModel by viewModels()
    private lateinit var mAllLogicAdapter: TextAdapter<LogicEntity>
    private lateinit var mNowLogicAdapter: TextAdapter<LogicEntity>

    override fun getLayoutId(): Int {
        return R.layout.fragment_function_detail
    }

    override fun getTitleRes(): Int {
        return R.string.function_detail
    }

    override fun getMenuRes(): Int {
        return R.menu.menu_function_detail
    }

    override fun onMenuItemClick(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.action_add -> {

            }

            R.id.action_delete -> {

            }

            R.id.action_detail -> {

            }

            R.id.action_trigger -> {

            }

        }
    }

    override fun onBackPress() {
        findNavController().popBackStack()
    }

    override fun initData() {
        super.initData()
        viewModel.initFunctionData(args.functionId)

        //展示全部的逻辑的
        binding.allRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mAllLogicAdapter = TextAdapter(mCallBack = object : CallBack<LogicEntity> {
            override fun onCallBack(data: LogicEntity) {
                viewModel.selectLogicEntity = data
            }
        })
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.allLogicFlow.collect {
                    val list = it.map { data ->
                        ICheckTextWrap(data) {
                            data.keyTag
                        }
                    }
                    mAllLogicAdapter.upData(list)
                }
            }
        }

        //展示当前会进行判断的逻辑
        binding.nowRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mNowLogicAdapter = TextAdapter()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.allLogicFlow.collect {
                    val list = it.map { data ->
                        ICheckTextWrap(data) {
                            data.keyTag
                        }
                    }
                    mAllLogicAdapter.upData(list)
                }
            }
        }


    }


}