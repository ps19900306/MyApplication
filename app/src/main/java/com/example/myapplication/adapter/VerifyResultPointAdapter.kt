package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

import com.example.myapplication.databinding.ItemVerifyResultPointBinding
import com.nwq.opencv.data.PointVerifyResult

class VerifyResultPointAdapter(val list: List<PointVerifyResult>) :
    RecyclerView.Adapter<VerifyResultPointAdapter.ViewHolder>() {

    private val  mList = mutableListOf<Int>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVerifyResultPointBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list.getOrNull(position)?.let {
            holder.bindData(it)
        }
    }

    inner class ViewHolder(
        val binding: ItemVerifyResultPointBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(it: PointVerifyResult) {
            if (it.isPass){
                binding.root.setBackgroundResource(R.drawable.bg_st008000_sobbe4bb)
            }else{
                binding.root.setBackgroundResource(R.drawable.bg_st800000_soe4bbbb)
            }
            binding.pointTv.text = "(${it.x},${it.y})"
            binding.valueTv.text = "H:${it.h}, S:${it.s},V:${it.v}"
        }

    }
}