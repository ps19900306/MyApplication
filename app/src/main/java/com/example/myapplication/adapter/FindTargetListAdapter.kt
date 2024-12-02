package com.example.myapplication.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.example.myapplication.databinding.ItemFindTargetBinding
import com.nwq.opencv.db.entity.FindTargetRecord


class FindTargetListAdapter(

) : RecyclerView.Adapter<FindTargetListAdapter.ViewHolder>() {

    private val values = mutableListOf<FindTargetRecord>()

    fun updateData(list: List<FindTargetRecord>) {
        this.values.clear()
        this.values.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemFindTargetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ItemFindTargetBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }

}