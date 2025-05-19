package com.nwq.dialog


import android.view.LayoutInflater
import android.view.ViewGroup
import com.nwq.base.BaseDialogFragment
import com.nwq.baseutils.databinding.DialogSimpleTipsBinding

class SimpleTipsDialog(val titleRes: Int = 0,val descriptionRes: Int = 0, val onClick: (Boolean) -> Unit) :
    BaseDialogFragment<DialogSimpleTipsBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogSimpleTipsBinding {
        return DialogSimpleTipsBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        if (titleRes != 0){
            binding.title.setText(titleRes)
        }
        if (descriptionRes != 0){
            binding.description.setText(descriptionRes)
        }
        binding.confirmButton.setOnClickListener {
            dismissDialog()
            onClick.invoke(true)
        }
        binding.cancelButton.setOnClickListener {
            dismissDialog()
            onClick.invoke(false)
        }
    }


}