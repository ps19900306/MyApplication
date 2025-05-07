package com.example.myapplication.function

import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.databinding.FragmentFunctionDetailBinding
import com.nwq.base.BaseFragment


class FunctionDetailFragment : BaseFragment<FragmentFunctionDetailBinding>() {


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFunctionDetailBinding {
       return FragmentFunctionDetailBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        super.initData()
        val edgeStyle = Paint().apply {
            isAntiAlias = true
            strokeWidth = 5f
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            pathEffect = CornerPathEffect(10f)
        }
    }
}