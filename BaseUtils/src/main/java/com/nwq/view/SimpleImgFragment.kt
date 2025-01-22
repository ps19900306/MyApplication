package com.nwq.view

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import com.nwq.base.BaseDialogFragment
import com.nwq.baseutils.databinding.FragmentSimpleImgBinding


class SimpleImgFragment(val bitmap: Bitmap) : BaseDialogFragment<FragmentSimpleImgBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSimpleImgBinding {
        return FragmentSimpleImgBinding.inflate(inflater)
    }

    override fun initData() {
        //不能外部点击关掉
        binding.img.setImageBitmap(bitmap)
        binding.sureBtn.setOnClickListener {
            dismissDialog()
        }
    }

    // 是否允许点击外部取消对话框
    override fun isCancelableOutside(): Boolean = false
}