package com.example.myapplication.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.myapplication.databinding.ItemFindTargetBinding
import com.nwq.baseutils.singleClick
import com.nwq.opencv.db.entity.FindTargetHsvEntity
import com.nwq.opencv.db.entity.FindTargetImgEntity
import com.nwq.opencv.db.entity.FindTargetMatEntity
import com.nwq.opencv.db.entity.FindTargetRecord
import com.nwq.opencv.db.entity.FindTargetRgbEntity


class FindTargetListAdapter(

) : RecyclerView.Adapter<FindTargetListAdapter.ViewHolder>() {

    private val values = mutableListOf<FindTargetRecord>()

    private var mFindTargetListLister: FindTargetListLister? = null

    fun setFindTargetListLister(ll: FindTargetListLister) {
        mFindTargetListLister = ll
    }





    fun updateData(list: List<FindTargetRecord>) {
        this.values.clear()
        this.values.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mFindTargetListLister,
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

    inner class ViewHolder(val ll: FindTargetListLister? ,val binding: ItemFindTargetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.deleteTv.singleClick {
                ll?.ondDelete(data!!)
            }

        }

        var data: FindTargetRecord? = null

        fun bindData(item: FindTargetRecord) {
            data=item
        }

    }


}