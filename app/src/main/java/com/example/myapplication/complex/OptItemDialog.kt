package com.example.myapplication.complex

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.DialogOptItemBinding
import com.nwq.base.BaseDialogFragment
import com.nwq.callback.CallBack
import com.nwq.simplelist.IText
import com.nwq.simplelist.TextAdapter
import com.nwq.simplelist.TextResWarp
import com.example.myapplication.R

class OptItemDialog : BaseDialogFragment<DialogOptItemBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogOptItemBinding {
        return DialogOptItemBinding.inflate(inflater, container, false)
    }

    private var callBack: CallBack<Int>? = null

    public fun setCallBack(callBack: CallBack<Int>): OptItemDialog {
        this.callBack = callBack
        return this
    }


    override fun initData() {
        binding.binarizationRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        val binarizationAdapter = TextAdapter(mCallBack = callBack)
        binding.binarizationRecyclerView.adapter = binarizationAdapter
        binarizationAdapter.upData(
            listOf<IText<Int>>(
                TextResWarp(R.string.grayscale_binarization),
                TextResWarp(R.string.h_s_v_binarization),
            )
        )
        binding.corpRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        val corpAdapter = TextAdapter(mCallBack = callBack)
        binding.corpRecyclerView.adapter = corpAdapter
        corpAdapter.upData(listOf<IText<Int>>(
            TextResWarp(R.string.merge_and_crop),
            TextResWarp(R.string.separate_cropping),
        ))
    }
}