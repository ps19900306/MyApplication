package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

import com.example.myapplication.databinding.ItemVerifyResultIndexBinding
import com.nwq.baseutils.singleClick
import com.nwq.callback.CallBack
import com.nwq.opencv.db.entity.TargetVerifyResult

class TargetVerifyResultAdapter(
    private val mCallBack: CallBack<Int>? = null,
) : RecyclerView.Adapter<TargetVerifyResultAdapter.ViewHolder>() {


    private val list = mutableListOf<TargetVerifyResult>()

    public fun updateData(list: List<TargetVerifyResult>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVerifyResultIndexBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val holder =ViewHolder(binding)
        binding.root.singleClick {
            mCallBack?.onCallBack(holder.absoluteAdapterPosition)
        }
        return holder
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
        val binding: ItemVerifyResultIndexBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(it: TargetVerifyResult) {
            if (it.isDo){
                if (it.isEffective){
                    if (it.hasFind){
                        binding.root.setBackgroundResource(R.drawable.bg_st008000_sobbe4bb)
                    }else{
                        binding.root.setBackgroundResource(R.drawable.bg_st800000_soe4bbbb)
                    }
                }
            }else{
                binding.root.setBackgroundResource(R.drawable.bg_st333_sobbb)
            }
            binding.fileName.text = it.ImgName
            if (it.hasFind){
                binding.isPassTv.text = binding.root.context.getText( com.nwq.baseutils.R.string.has_find_t)
            }else{
                binding.isPassTv.text = binding.root.context.getText( com.nwq.baseutils.R.string.un_has_find_t)
            }
        }
    }
}