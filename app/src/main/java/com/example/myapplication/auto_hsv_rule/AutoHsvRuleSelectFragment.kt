package com.example.myapplication.auto_hsv_rule


import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentSearchListBinding
import com.nwq.base.BaseToolBar2Fragment
import com.nwq.opencv.db.entity.AutoRulePointEntity
import com.nwq.simplelist.CheckTextAdapter
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.launch
import com.example.myapplication.R
import com.nwq.callback.CallBack
import com.nwq.constant.ConstantKeyStr
import com.nwq.opencv.IAutoRulePoint
import com.nwq.opencv.auto_point_impl.CodeHsvRuleUtils
import com.nwq.opencv.db.IdentifyDatabase
import com.nwq.opencv.hsv.HSVRule
import com.nwq.simplelist.TextAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn

/**
 *[AutoRulePointEntity]
 */
class AutoHsvRuleSelectFragment : BaseToolBar2Fragment<FragmentSearchListBinding>() {

    private val args: AutoHsvRuleSelectFragmentArgs by navArgs()
    private lateinit var mTextAdapter: TextAdapter<IAutoRulePoint>
    private val queryFlow: MutableStateFlow<String> = MutableStateFlow("")
    private val mAutoRulePointDao = IdentifyDatabase.getDatabase().autoRulePointDao()

    // 合并查询逻辑
    val resultsFlow = queryFlow.debounce(1000).flatMapLatest { query ->
        if (query.isEmpty()) {
            mAutoRulePointDao.findAll() // 如果输入为空，查询整个表
        } else {
            mAutoRulePointDao.findByKeyTagLike(query) // 如果输入不为空，进行模糊查询
        }
    }.flowOn(Dispatchers.IO)

    fun updateSearchStr(string: String) {
        queryFlow.value = string
    }

    override fun createBinding(inflater: LayoutInflater): FragmentSearchListBinding {
        return FragmentSearchListBinding.inflate(inflater)
    }


    override fun getMenuRes(): Int {
        return R.menu.menu_list_select
    }


    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_select_all -> {
                //  mTextAdapter.selectAll(true)
                return true
            }

            R.id.action_delete_all -> {
                //  mTextAdapter.selectAll(false)
                return true
            }

            R.id.action_reverse_all -> {
                //  mTextAdapter.selectReverse()
                return true
            }
        }
        return false
    }


    override fun onBackPress(): Boolean {

        return false
    }

    override fun initView() {
        super.initView()
        mTextAdapter = TextAdapter(mCallBack = object : CallBack<IAutoRulePoint> {
            override fun onCallBack(data: IAutoRulePoint) {
                val result = Bundle().apply {
                    putString(
                        ConstantKeyStr.SELECTED_RESULT,
                        data.getTag()
                    )
                }
                Log.i("AutoHsvRuleSelectFragment", "${args.actionTag}:${data.getTag()}")
                parentFragment?.setFragmentResult(args.actionTag, result)
                findNavController().popBackStack()
            }
        })
        binding.recycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recycler.adapter = mTextAdapter
        binding.inputEdit.addTextChangedListener {
            val text = it?.toString() ?: ""
            updateSearchStr(text)
        }
    }


    override fun initData() {
        super.initData()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                resultsFlow.collect {
                    val list = it.map { data ->
                        ICheckTextWrap<IAutoRulePoint>(data) {
                            "${data.keyTag}:详情:${data.description}"
                        }
                    }.toMutableList()
                    if (TextUtils.isEmpty(queryFlow.value)) {
                        val list2 = CodeHsvRuleUtils.mAutoRulePointList.map { data ->
                            ICheckTextWrap<IAutoRulePoint>(data) {
                                "${data.getTag()}:详情:${data.getDescriptionInfo()}"
                            }
                        }
                        list.addAll(0, list2)
                    } else {
                        val list2 = CodeHsvRuleUtils.mAutoRulePointList.filter { data ->
                            data.getTag().contains(queryFlow.value)
                        }.map { data ->
                            ICheckTextWrap<IAutoRulePoint>(data) {
                                "${data.getTag()}:详情:${data.getDescriptionInfo()}"
                            }
                        }
                        list.addAll(0, list2)
                    }
                    mTextAdapter.upData(list)
                }
            }
        }
    }
}