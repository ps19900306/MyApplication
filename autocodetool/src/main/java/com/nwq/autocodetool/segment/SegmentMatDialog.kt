package com.nwq.autocodetool.segment

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.TextureView
import android.view.ViewGroup
import com.nwq.autocodetool.databinding.DialogSegmentMatBinding
import com.nwq.base.BaseDialogFragment
import com.nwq.baseutils.singleClick
import com.nwq.optlib.bean.SegmentMatInfo

class SegmentMatDialog(val matInfo: SegmentMatInfo) :
    BaseDialogFragment<DialogSegmentMatBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogSegmentMatBinding {
        return DialogSegmentMatBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        if (!TextUtils.isEmpty(matInfo.flagStr)) {
            binding.editSpacingHeight.setText(matInfo.flagStr)
        }
        binding.img.setImageBitmap(matInfo.mBitmap)
        binding.btnConfirm.singleClick {
            matInfo.flagStr = binding.editSpacingHeight.text.toString()
            dismissDialog()
        }
        binding.btnCancel.singleClick {
            dismissDialog()
        }

    }
}