package com.nwq.autocodetool.index

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nwq.autocodetool.AppToolBarFragment
import com.nwq.autocodetool.databinding.FragmentGrayscaleBinarizationBinding
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.singleClick
import com.nwq.optlib.bean.GrayRule
import com.nwq.optlib.bean.HSVRule
import com.nwq.optlib.db.bean.GrayFilterRuleDb
import com.nwq.optlib.db.bean.HsvFilterRuleDb
import com.nwq.simplelist.ICheckTextWrap
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.opencv.core.Mat


class GrayscaleBinarizationFragment : AppToolBarFragment<FragmentGrayscaleBinarizationBinding>() {

    private val args: GrayscaleBinarizationFragmentArgs by navArgs()
    private val viewModel: ComplexRecognitionViewModel by viewModels({ requireActivity() })

    private val grayMat: Mat? by lazy {
        viewModel.getGrayMat(viewModel.getIndex(GrayFilterRuleDb::class.java))
    }

    private val list = mutableListOf<GrayRule>()
    private val updateSignalFlow: MutableStateFlow<Int> = MutableStateFlow(Int.MIN_VALUE)


    private var minI: Int = 0
    private var maxI: Int = 255

    override fun createBinding(inflater: LayoutInflater): FragmentGrayscaleBinarizationBinding {
        return FragmentGrayscaleBinarizationBinding.inflate(inflater)
    }

    //实际并不会使用 只是保持格式
    override fun getMenuRes(): Int {
        return NO_MENU
    }

    //实际并不会使用 只是保持格式
    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        return true
    }


    override fun onResume() {
        super.onResume()
        fullScreen()
    }

    override fun onBackPress(): Boolean {
        findNavController().popBackStack()
        return true
    }

    override fun initView() {
        super.initView()
        setupSeekBarAndEditText(
            binding.sbMin,
            binding.etMin,
            0,
            255,
        ) { i ->
            minI = i
            sendUpdateSignal()
        }
        setupSeekBarAndEditText(
            binding.sbMax,
            binding.etMax,
            0,
            255,
        ) { i ->
            maxI = i
            sendUpdateSignal()
        }
        binding.saveBtn.singleClick {
            list.add(GrayRule(if (minI > maxI) maxI else minI, if (minI > maxI) minI else maxI))
            val grayFilterRule = GrayFilterRuleDb().apply {
                this.ruleList = list
            }
            viewModel.checkAndAddOpt(grayFilterRule, GrayFilterRuleDb::class.java)
            onBackPress()
        }

        binding.addBtn.singleClick {
            list.add(GrayRule(if (minI > maxI) maxI else minI, if (minI > maxI) minI else maxI))
            minI = 0
            maxI = 255
            binding.etMin.setText("0")
            binding.etMax.setText("255")
            binding.sbMin.progress = 0
            binding.sbMax.progress = 255
        }
    }

    override fun initData() {
        super.initData()
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                updateSignalFlow.collectLatest {
                    delay(500)
                    if (grayMat == null)
                        return@collectLatest
                    val mat = if (minI > maxI) {
                        MatUtils.thresholdByRange(grayMat!!, maxI, minI)
                    } else {
                        MatUtils.thresholdByRange(grayMat!!, minI, maxI)
                    }
                    binding.imgCountTv.text = "过滤点数： ${MatUtils.countNonZero(mat)}"
                    binding.imageView.setImageBitmap(MatUtils.grayMatToBitmap(mat))
                }
            }
        }
//        viewModel.getMatResultByClass(GrayFilterRuleDb::class.java)?.let { db ->
//            db.ruleList.forEach {
//                list.add(GrayRule(it.min, it.max))
//            }
//        }
    }


    private fun setupSeekBarAndEditText(
        seekBar: SeekBar,
        editText: EditText,
        min: Int,
        max: Int,
        updateFlow: ((Int) -> Unit)? = null
    ) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    editText.setText(progress.toString())
                    updateFlow?.invoke(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    return
                }
                val strValue = s.toString();
                if (strValue.isEmpty())
                    return

                val value = strValue.toInt()
                if (value in min..max) {
                    seekBar.progress = value
                    updateFlow?.invoke(value)
                }
            }
        })
    }

    private fun sendUpdateSignal() {
        updateSignalFlow.value += 1
    }
}