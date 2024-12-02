package com.example.myapplication.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.myapplication.databinding.ItemFindTargetBinding
import com.nwq.opencv.db.entity.FindTargetHsvEntity
import com.nwq.opencv.db.entity.FindTargetImgEntity
import com.nwq.opencv.db.entity.FindTargetMatEntity
import com.nwq.opencv.db.entity.FindTargetRecord
import com.nwq.opencv.db.entity.FindTargetRgbEntity


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

    override fun onBindViewHolder(holder: FindTargetListAdapter.ViewHolder, position: Int) {
        val item = values[position]
        holder.bindData(item)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val binding: ItemFindTargetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

        }

        fun bindData(item: FindTargetRecord) {

            binding.rgbBtn.visibility = View.GONE
            binding.hsvBtn.visibility = View.GONE
            binding.imgBtn.visibility = View.GONE
            binding.matBtn.visibility = View.GONE

            item.list.forEach {
                when (it) {
                    is FindTargetRgbEntity -> {
                        binding.rgbBtn.visibility = View.VISIBLE
                    }

                    is FindTargetHsvEntity -> {
                        binding.hsvBtn.visibility = View.VISIBLE
                    }

                    is FindTargetImgEntity -> {
                        binding.imgBtn.visibility = View.VISIBLE
                    }

                    is FindTargetMatEntity -> {
                        binding.matBtn.visibility = View.VISIBLE
                    }
                }
            }
        }



    }

}