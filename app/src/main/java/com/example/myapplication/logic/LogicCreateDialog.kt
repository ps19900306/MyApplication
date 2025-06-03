package com.example.myapplication.logic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.myapplication.R
import com.example.myapplication.databinding.DialogCreateLogicBinding
import com.nwq.base.BaseDialogFragment
import com.nwq.baseutils.T
import com.nwq.baseutils.singleClick

class LogicCreateDialog(
    val size: Int = 0,
    val logicId: Long = 0,
    val result: (name: String, parentId: Long, offset: Int) -> Unit
) : BaseDialogFragment<DialogCreateLogicBinding>() {

    private var offset = 0

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogCreateLogicBinding {
        return DialogCreateLogicBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        if (logicId <= 0) {
            binding.isRootCheck.isChecked = true
            binding.isRootCheck.isFocusable = false
            binding.isRootCheck.isEnabled = false
        } else {
            binding.parentLogicTv.text = "父逻辑：$logicId"
            binding.isRootCheck.setOnCheckedChangeListener { _, isChecked ->
                binding.parentLogicTv.isVisible = !isChecked
            }
        }

        binding.priorityGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButton1 -> offset = 0
                R.id.radioButton2 -> offset = Int.MAX_VALUE / 4
                R.id.radioButton3 -> offset = Int.MAX_VALUE / 2
            }
        }
        binding.saveBtn.singleClick {
            val parentId = if (binding.isRootCheck.isChecked) {
                0
            } else {
                logicId
            }
            val name = binding.titleEdt.text.toString()
            if (name.isEmpty()) {
                T.show("请输入logic名称")
                return@singleClick
            }
            result.invoke(name, parentId, offset + size)
            dismissDialog()
        }

    }
}