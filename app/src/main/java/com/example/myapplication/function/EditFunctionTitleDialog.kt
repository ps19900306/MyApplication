package com.example.myapplication.function


import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.databinding.FragmentEditFunctionTitleBinding
import com.nwq.base.BaseDialogFragment
import com.nwq.baseutils.T
import com.nwq.baseutils.singleClick


class EditFunctionTitleDialog(
    val name: String? = null,
    val description: String? = null,
    val result: (name: String, description: String) -> Unit
) : BaseDialogFragment<FragmentEditFunctionTitleBinding>() {


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditFunctionTitleBinding {
        return FragmentEditFunctionTitleBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        super.initData()
        name?.let {
            binding.titleEdt.setText(it)
        }
        description?.let {
            binding.descriptionEdt.setText(it)
        }
        binding.saveBtn.singleClick {
            if (binding.titleEdt.text.toString().isEmpty()) {
                T.show("请输入标题")
                return@singleClick
            }
            if (binding.descriptionEdt.text.toString().isEmpty()) {
                T.show("请输入描述")
                return@singleClick
            }
            result.invoke(binding.titleEdt.text.toString(), binding.descriptionEdt.text.toString())
        }
    }


}