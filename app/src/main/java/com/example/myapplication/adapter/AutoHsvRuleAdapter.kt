package com.example.myapplication.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentAutoHsvRuleListBinding
import com.example.myapplication.databinding.ItemAutoHsvRuleListBinding
import com.example.myapplication.databinding.ItemFindTargetBinding
import com.nwq.opencv.IAutoRulePoint


class AutoHsvRuleAdapter : RecyclerView.Adapter<AutoHsvRuleAdapter.ViewHolder>() {

    private val list = mutableListOf<IAutoRulePoint>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAutoHsvRuleListBinding.inflate(
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
        val binding: ItemAutoHsvRuleListBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {


        fun bindData(item: IAutoRulePoint) {

        }

    }


}