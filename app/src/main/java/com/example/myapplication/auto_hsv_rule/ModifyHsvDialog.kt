package com.example.myapplication.auto_hsv_rule

import com.nwq.base.BaseDialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myapplication.R
import com.example.myapplication.adapter.ColorAdapter
import com.example.myapplication.databinding.FragmentSetSHVFilterDialogBinding
import com.nwq.baseutils.HsvRuleUtils
import com.nwq.baseutils.singleClick
import com.nwq.callback.CallBack
import com.nwq.opencv.hsv.HSVRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch


class ModifyHsvDialog(val defaultHsv: HSVRule, val callBack: CallBack<HSVRule>) :
    BaseDialogFragment<FragmentSetSHVFilterDialogBinding>() {
    private val TAG = ModifyHsvDialog::class.java.simpleName
    private var nowHsv: MutableStateFlow<HSVRule> = MutableStateFlow(defaultHsv).apply {
        debounce(1000)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSetSHVFilterDialogBinding {
        return FragmentSetSHVFilterDialogBinding.inflate(inflater)
    }


    override fun initData() {
        super.initData()
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
            callBack.onCallBack(nowHsv.value)
            dismissDialog()
        }

        setupSeekBarAndEditText(R.id.sb_hue_min, R.id.et_hue_min, 0, 180)
        { i ->
            nowHsv.value = HSVRule( i, defaultHsv.maxH, defaultHsv.minS, defaultHsv.maxS, defaultHsv.minV, defaultHsv.maxV)
        }
        setupSeekBarAndEditText(R.id.sb_hue_max, R.id.et_hue_max, 0, 180)
        { i ->
            nowHsv.value = HSVRule( defaultHsv.minH, i, defaultHsv.minS, defaultHsv.maxS, defaultHsv.minV, defaultHsv.maxV)
        }
        setupSeekBarAndEditText(
            R.id.sb_saturation_min,
            R.id.et_saturation_min,
            0,
            255,
        ){i->
            nowHsv.value = HSVRule( defaultHsv.minH, defaultHsv.maxH, i, defaultHsv.maxS, defaultHsv.minV, defaultHsv.maxV)
        }
        setupSeekBarAndEditText(
            R.id.sb_saturation_max,
            R.id.et_saturation_max,
            0,
            255,
        ){i->
            nowHsv.value = HSVRule( defaultHsv.minH, defaultHsv.maxH, defaultHsv.minS, i, defaultHsv.minV, defaultHsv.maxV)
        }
        setupSeekBarAndEditText(
            R.id.sb_value_min,
            R.id.et_value_min,
            0,
            255,
        ){i->
            nowHsv.value = HSVRule( defaultHsv.minH, defaultHsv.maxH, defaultHsv.minS, defaultHsv.maxS, i, defaultHsv.maxV)
        }
        setupSeekBarAndEditText(
            R.id.sb_value_max,
            R.id.et_value_max,
            0,
            255,
        ){
            i->
            nowHsv.value = HSVRule( defaultHsv.minH, defaultHsv.maxH, defaultHsv.minS, defaultHsv.maxS, defaultHsv.minV, i)
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                nowHsv.collectLatest {
                    binding.recycler.adapter = ColorAdapter(HsvRuleUtils.getColorsList(it.minH, it.maxH, it.minS, it.maxS, it.minV, it.maxV))
                }
            }
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