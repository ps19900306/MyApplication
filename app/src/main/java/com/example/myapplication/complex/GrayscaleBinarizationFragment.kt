package com.example.myapplication.complex

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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.adapter.ColorAdapter
import com.example.myapplication.base.AppToolBarFragment
import com.example.myapplication.databinding.FragmentGrayscaleBinarizationBinding
import com.example.myapplication.find_target.FindTargetDetailFragmentArgs
import com.nwq.baseutils.HsvRuleUtils
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.singleClick
import com.nwq.opencv.opt.BinarizationByGray
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.opencv.core.Mat


class GrayscaleBinarizationFragment : AppToolBarFragment<FragmentGrayscaleBinarizationBinding>() {

    private val args: GrayscaleBinarizationFragmentArgs by navArgs()
    private val viewModel: ComplexRecognitionViewModel by viewModels({ requireActivity() })

    private val grayMat: Mat? by lazy {
        viewModel.getGrayMat(args.isModify)
    }
    private val updateSignalFlow: MutableStateFlow<Int> = MutableStateFlow(Int.MIN_VALUE)


    private var minI: Int = 0
    private var maxI: Int = 255

    override fun createBinding(inflater: LayoutInflater): FragmentGrayscaleBinarizationBinding {
        return FragmentGrayscaleBinarizationBinding.inflate(inflater)
    }

    //实际并不会使用 只是保持格式
    override fun getMenuRes(): Int {
        return R.menu.menu_list_edit
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
            lifecycleScope.launch {
                viewModel.addOptStep(BinarizationByGray(if (minI > maxI) maxI else minI, if (minI > maxI) minI else maxI))
            }
            onBackPress()
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
                    binding.imageView.setImageBitmap(MatUtils.grayMatToBitmap(mat))
                }
            }
        }
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