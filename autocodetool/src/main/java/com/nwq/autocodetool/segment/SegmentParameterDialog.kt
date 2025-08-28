package com.nwq.autocodetool.segment


import android.view.LayoutInflater
import android.view.ViewGroup
import com.nwq.autocodetool.databinding.DialogSegmentParameterBinding
import com.nwq.base.BaseDialogFragment
import com.nwq.callback.CallBack

/**
 * 分割图像的参数设置
 */
class SegmentParameterDialog : BaseDialogFragment<DialogSegmentParameterBinding>() {

    private var callBack: CallBack<IntArray>? = null

    private var defaultParameter = intArrayOf(-1, -1, -1, -1)

    public fun setCallBack(callBack: CallBack<IntArray>): SegmentParameterDialog {
        this.callBack = callBack
        return this
    }

    public fun setDefaultParameter(parameter: IntArray): SegmentParameterDialog {
        this.defaultParameter = parameter
        return this
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogSegmentParameterBinding {
        return DialogSegmentParameterBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.editMinW.setText("${defaultParameter[0]}")
        binding.editMaxW.setText("${defaultParameter[1]}")
        binding.editMinH.setText("${defaultParameter[2]}")
        binding.editMaxH.setText("${defaultParameter[3]}")


        binding.btnConfirm.setOnClickListener {
            val minW =
                if (binding.editMinW.text.isNullOrEmpty()) -1 else binding.editMinW.text.toString()
                    .toInt()
            val maxW =
                if (binding.editMaxW.text.isNullOrEmpty()) -1 else binding.editMaxW.text.toString()
                    .toInt()
            val minH =
                if (binding.editMinH.text.isNullOrEmpty()) -1 else binding.editMinH.text.toString()
                    .toInt()
            val maxH =
                if (binding.editMaxH.text.isNullOrEmpty()) -1 else binding.editMaxH.text.toString()
                    .toInt()

            callBack?.onCallBack(intArrayOf(minW, maxW, minH, maxH))
            dismiss()
        }
    }
}