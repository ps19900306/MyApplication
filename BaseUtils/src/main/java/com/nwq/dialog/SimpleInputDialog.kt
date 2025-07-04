package com.nwq.dialog


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.nwq.base.BaseDialogFragment
import com.nwq.baseutils.databinding.DialogSimpleInputBinding
import com.nwq.baseutils.databinding.DialogSimpleTipsBinding

class SimpleInputDialog(
    val titleRes: Int = 0,
    val defaultlStr1: String = "",
    val hintStr1: String = "",
    val onClick: (name: String) -> Unit
) : BaseDialogFragment<DialogSimpleInputBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogSimpleInputBinding {
        return DialogSimpleInputBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        if (titleRes != 0) {
            binding.title.setText(titleRes)
        }
        binding.inputEdit1.setText(defaultlStr1)
        binding.inputEdit1.hint = hintStr1
        binding.inputEdit2.isVisible = false;
        binding.confirmButton.setOnClickListener {
            val str1 = binding.inputEdit1.text.toString()
            if (str1.isEmpty()) {
                return@setOnClickListener
            }
            dismissDialog()
            onClick.invoke(str1)
        }
        binding.cancelButton.setOnClickListener {
            dismissDialog()
        }
    }


}