package com.nwq.view

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.nwq.base.BaseDialogFragment
import com.nwq.baseutils.databinding.FragmentSimpleImgBinding


class SimpleImgFragment(val bitmap1: Bitmap, val bitmap2: Bitmap?=null, val bitmap3: Bitmap?=null) : BaseDialogFragment<FragmentSimpleImgBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSimpleImgBinding {
        return FragmentSimpleImgBinding.inflate(inflater)
    }

    override fun initData() {
        //不能外部点击关掉
        binding.img.setImageBitmap(bitmap1)
        bitmap2?.let {
            binding.img2.setImageBitmap(it)
            binding.img2.isVisible = true
        }
        bitmap3?.let {
            binding.img3.setImageBitmap(it)
            binding.img3.isVisible = true
        }
        binding.sureBtn.setOnClickListener {
            dismissDialog()
        }
    }

    // 是否允许点击外部取消对话框
    override fun isCancelableOutside(): Boolean = false
}