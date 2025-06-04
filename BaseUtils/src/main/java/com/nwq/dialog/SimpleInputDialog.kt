package com.nwq.dialog


import android.view.LayoutInflater
import android.view.ViewGroup
import com.nwq.base.BaseDialogFragment
import com.nwq.baseutils.databinding.DialogSimpleInputBinding
import com.nwq.baseutils.databinding.DialogSimpleTipsBinding

class SimpleInputDialog(
    val titleRes: Int = 0,
    val defaultlStr1: String = "",
    val defaultlStr2: String = "",
    val onClick: (name: String, description: String) -> Unit
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
        binding.inputEdit2.setText(defaultlStr2)
        binding.confirmButton.setOnClickListener {
            val str1 = binding.inputEdit1.text.toString()
            val str2 = binding.inputEdit2.text.toString()
            if (str1.isEmpty() || str2.isEmpty()) {
                return@setOnClickListener
            }
            dismissDialog()
            onClick.invoke(str1,str2)
        }
        binding.cancelButton.setOnClickListener {
            dismissDialog()
            onClick.invoke(defaultlStr1,defaultlStr2)
        }
    }


}