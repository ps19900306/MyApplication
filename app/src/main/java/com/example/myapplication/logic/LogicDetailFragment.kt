package com.example.myapplication.logic

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLogicDetailBinding
import com.example.myapplication.function.FunctionEdtViewModel
import com.nwq.base.BaseToolBarFragment
import com.nwq.opencv.constant.LogicJudeResult
import com.nwq.opencv.db.entity.LogicEntity
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [LogicDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LogicDetailFragment : BaseToolBarFragment<FragmentLogicDetailBinding>() {

    private val args: LogicDetailFragmentArgs by navArgs()

    private val viewModel: LogicDetailViewModel by viewModels()



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
    }

    override fun initData() {
        super.initData()
        lifecycleScope.launch {
            val data = viewModel.initLogicEntity(args.logicId)
            data?.let {initLogicEntity(it) }

        }
    }

    private fun initLogicEntity(data: LogicEntity) {
        binding.resultSpinner.setSelection(viewModel.getResultSelection())
        setTitleString(data.keyTag)

    }

    private fun  showUiGroup(isFunction:Boolean){
        binding.functionGroup.visibility = if (isFunction) View.VISIBLE else View.GONE
        binding.normalGroup.visibility = if (isFunction) View.GONE else View.VISIBLE
    }
}