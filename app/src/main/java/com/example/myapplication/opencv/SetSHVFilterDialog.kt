package com.example.myapplication.opencv

import com.nwq.base.BaseDialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSetSHVFilterDialogBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SetSHVFilterDialog() : BaseDialogFragment<FragmentSetSHVFilterDialogBinding>() {
    private val TAG = SetSHVFilterDialog::class.java.simpleName
    private val viewModel by viewModels<OpenCvOptModel>({ requireActivity() })
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSetSHVFilterDialogBinding {
        return FragmentSetSHVFilterDialogBinding.inflate(inflater)
    }


    override fun initData() {
        super.initData()
        binding.etHueMin.setText("${viewModel.getMinH()}")
        binding.etHueMax.setText("${viewModel.getMaxH()}")
        binding.etSaturationMin.setText("${viewModel.getMinS()}")
        binding.etSaturationMax.setText("${viewModel.getMaxS()}")
        binding.etValueMin.setText("${viewModel.getMinV()}")
        binding.etValueMax.setText("${viewModel.getMaxV()}")

        binding.sbHueMin.progress = viewModel.getMinH()
        binding.sbHueMax.progress = viewModel.getMaxH()
        binding.sbSaturationMin.progress = viewModel.getMinS()
        binding.sbSaturationMax.progress = viewModel.getMaxS()
        binding.sbValueMin.progress = viewModel.getMinV()
        binding.sbValueMax.progress = viewModel.getMaxV()


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