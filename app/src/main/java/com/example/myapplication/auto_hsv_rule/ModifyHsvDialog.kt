package com.example.myapplication.auto_hsv_rule

import android.graphics.Bitmap
import com.nwq.base.BaseDialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myapplication.R
import com.example.myapplication.adapter.ColorAdapter
import com.example.myapplication.databinding.FragmentSetSHVFilterDialogBinding
import com.nwq.baseutils.HsvRuleUtils
import com.nwq.baseutils.MatUtils
import com.nwq.baseutils.MatUtils.filterByMask
import com.nwq.baseutils.singleClick
import com.nwq.callback.CallBack
import com.nwq.opencv.hsv.HSVRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch


class ModifyHsvDialog(
    val defaultHsv: HSVRule = HSVRule(),
    val bitmap: Bitmap? = null,
    val callBack: CallBack<HSVRule>
) :
    BaseDialogFragment<FragmentSetSHVFilterDialogBinding>() {
    private val TAG = ModifyHsvDialog::class.java.simpleName

    private val srcMat by lazy {
        if (bitmap != null) {
            MatUtils.bitmapToHsvMat(bitmap)
        } else {
            null
        }
    }

    private val updateSignalFlow: MutableStateFlow<Int> = MutableStateFlow(Int.MIN_VALUE)


    private var minH: Int = defaultHsv.minH
    private var maxH: Int = defaultHsv.maxH
    private var minS: Int = defaultHsv.minS
    private var maxS: Int = defaultHsv.maxS
    private var minV: Int = defaultHsv.minV
    private var maxV: Int = defaultHsv.maxV

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSetSHVFilterDialogBinding {
        return FragmentSetSHVFilterDialogBinding.inflate(inflater)
    }

    private fun sendUpdateSignal() {
        updateSignalFlow.value += 1
    }

    override fun initData() {
        binding.etHueMin.setText("${defaultHsv.minH}")
        binding.etHueMax.setText("${defaultHsv.maxH}")
        binding.etSaturationMin.setText("${defaultHsv.minS}")
        binding.etSaturationMax.setText("${defaultHsv.maxS}")
        binding.etValueMin.setText("${defaultHsv.minV}")
        binding.etValueMax.setText("${defaultHsv.maxV}")

        binding.sbHueMin.progress = defaultHsv.minH
        binding.sbHueMax.progress = defaultHsv.maxH
        binding.sbSaturationMin.progress = defaultHsv.minS
        binding.sbSaturationMax.progress = defaultHsv.maxS
        binding.sbValueMin.progress = defaultHsv.minV
        binding.sbValueMax.progress = defaultHsv.maxV
        binding.saveBtn.singleClick {
            defaultHsv.minH = minH
            defaultHsv.maxH = maxH
            defaultHsv.minS = minS
            defaultHsv.maxS = maxS
            defaultHsv.minV = minV
            defaultHsv.maxV = maxV
            callBack.onCallBack(defaultHsv)
            dismissDialog()
        }
        binding.saveBtn.isVisible = true
        setupSeekBarAndEditText(R.id.sb_hue_min, R.id.et_hue_min, 0, 180)
        { i ->
            minH = i
            sendUpdateSignal()
        }
        setupSeekBarAndEditText(R.id.sb_hue_max, R.id.et_hue_max, 0, 180)
        { i ->
            maxH = i
            sendUpdateSignal()
        }
        setupSeekBarAndEditText(
            R.id.sb_saturation_min,
            R.id.et_saturation_min,
            0,
            255,
        ) { i ->
            minS = i
            sendUpdateSignal()
        }
        setupSeekBarAndEditText(
            R.id.sb_saturation_max,
            R.id.et_saturation_max,
            0,
            255,
        ) { i ->
            maxS = i
            sendUpdateSignal()
        }
        setupSeekBarAndEditText(
            R.id.sb_value_min,
            R.id.et_value_min,
            0,
            255,
        ) { i ->
            minV = i
            sendUpdateSignal()
        }
        setupSeekBarAndEditText(
            R.id.sb_value_max,
            R.id.et_value_max,
            0,
            255,
        ) { i ->
            maxV = i
            sendUpdateSignal()
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                updateSignalFlow.collectLatest {
                    delay(500)
                    binding.recycler.adapter = ColorAdapter(
                        HsvRuleUtils.getColorsList(
                            minH,
                            maxH,
                            minS,
                            maxS,
                            minV,
                            maxV
                        )
                    )

                    srcMat?.let { mat ->
                        val maskMat = MatUtils.getFilterMaskMat(
                            mat,
                            minH,
                            maxH,
                            minS,
                            maxS,
                            minV,
                            maxV
                        );
                        binding.imgCountTv.text = "过滤点数： ${MatUtils.countNonZero(maskMat)}"
                        val newBitmap = MatUtils.hsvMatToBitmap(MatUtils.filterByMask(mat, maskMat))
                        binding.srcImg.setImageBitmap(newBitmap)
                    }
                }
            }
        }


        bitmap?.let {
            binding.imgGroup.isVisible = true
            binding.srcImg.setImageBitmap(it)
            binding.imgSizeTv.text = "图片大小： ${it.width} x ${it.height}"
        }

    }


    private fun setupSeekBarAndEditText(
        seekBarId: Int,
        editTextId: Int,
        min: Int,
        max: Int,
        updateFlow: ((Int) -> Unit)? = null
    ) {
        val seekBar = findViewById<SeekBar>(seekBarId)
        val editText = findViewById<EditText>(editTextId)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                editText.setText(progress.toString())
                updateFlow?.invoke(progress)
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

}