package com.example.myapplication.auto_hsv_rule

import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.HsvRuleAdapter
import com.example.myapplication.databinding.FragmentAutoHsvRuleDetailBinding
import com.nwq.base.BaseFragment
import com.nwq.baseutils.FileUtils
import com.nwq.baseutils.singleClick
import com.nwq.callback.CallBack
import com.nwq.constant.ConstantKeyStr
import com.nwq.opencv.db.entity.AutoRulePointEntity
import com.nwq.opencv.hsv.HSVRule
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [AutoHsvRuleDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AutoHsvRuleDetailFragment : BaseFragment<FragmentAutoHsvRuleDetailBinding>(),
    CallBack<HSVRule> {

    private val TAG = "AutoHsvRuleDetailFragment"
    private val args: AutoHsvRuleDetailFragmentArgs by navArgs()
    private val viewModel by viewModels<AutoHsvRuleModel>({ requireActivity() })
    private val hsvRuleAdapter = HsvRuleAdapter()
    private lateinit var autoRulePointEntity: AutoRulePointEntity
    private var srcBitmap: Bitmap? = null
    private val hsvList by lazy {
        mutableListOf<HSVRule>()
    }
    private lateinit var adapter:HsvRuleAdapter

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAutoHsvRuleDetailBinding {
        return FragmentAutoHsvRuleDetailBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        super.initData()
        adapter = HsvRuleAdapter()
        if (!TextUtils.isEmpty(args.autoHsvRuleTag)) {
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED) {
                    viewModel.getByTagFlow(args.autoHsvRuleTag!!)?.collect {
                        autoRulePointEntity = it
                        initView(it)
                    }
                }
            }
        } else {
            val bitmap =
                FileUtils.loadBitmapFromGallery(ConstantKeyStr.AUTO_HSV_RULE_IMG_NAME) ?: return
            binding.srcImg.setImageBitmap(bitmap)
            srcBitmap = bitmap
            autoRulePointEntity = AutoRulePointEntity()
        }
        binding.addBtn.singleClick {
            addHsvRule()
        }
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter
    }

    private fun addHsvRule() {
        val dialog = ModifyHsvDialog(HSVRule(), srcBitmap, this)
        dialog.show(childFragmentManager, "ModifyHsvDialog")
    }


    private fun initView(autoRulePointEntity: AutoRulePointEntity) {
        hsvRuleAdapter.updateData(autoRulePointEntity.prList)
    }

    override fun onCallBack(data: HSVRule) {
        Log.i(TAG, "添加新的HSVRule: $data")
        hsvList.add(data)
        adapter.updateData(hsvList)
    }
}