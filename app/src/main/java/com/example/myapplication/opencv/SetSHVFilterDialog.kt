package com.example.myapplication.opencv

import BaseDialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import com.example.myapplication.R


class SetSHVFilterDialog : BaseDialogFragment() {

    private val viewModel by viewModels<OpenCvOptModel>({ requireActivity() })


    override fun getLayoutId(): Int {
        return R.layout.fragment_set_s_h_v_filter_dialog
    }


    override fun setupView(view: View) {
        super.setupView(view)
        setupSeekBarAndEditText(R.id.sb_hue_min, R.id.et_hue_min, 0, 180, viewModel::upDataMinHFlow)
        setupSeekBarAndEditText(R.id.sb_hue_max, R.id.et_hue_max, 0, 180, viewModel::upDataMaxHFlow)
        setupSeekBarAndEditText(
            R.id.sb_saturation_min,
            R.id.et_saturation_min,
            0,
            255,
            viewModel::upDataMinSFlow
        )
        setupSeekBarAndEditText(
            R.id.sb_saturation_max,
            R.id.et_saturation_max,
            0,
            255,
            viewModel::upDataMaxSFlow
        )
        setupSeekBarAndEditText(
            R.id.sb_value_min,
            R.id.et_value_min,
            0,
            255,
            viewModel::upDataMinVFlow
        )
        setupSeekBarAndEditText(
            R.id.sb_value_max,
            R.id.et_value_max,
            0,
            255,
            viewModel::upDataMaxVFlow
        )
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
                try {
                    val value = s.toString().toInt()
                    if (value in min..max) {
                        seekBar.progress = value
                        updateFlow?.invoke(value)
                    } else {
                        editText.error = "请输入 $min-$max 之间的值"
                    }
                } catch (e: NumberFormatException) {
                    editText.error = "请输入有效的数字"
                }
            }
        })
    }

}