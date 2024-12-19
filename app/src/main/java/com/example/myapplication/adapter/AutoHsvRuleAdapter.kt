package com.example.myapplication.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemAutoHsvRuleListBinding
import com.nwq.opencv.IAutoRulePoint


class AutoHsvRuleAdapter : RecyclerView.Adapter<AutoHsvRuleAdapter.ViewHolder>() {

    private val list = mutableListOf<IAutoRulePoint>()
    private val mSelectList = mutableListOf<IAutoRulePoint>()
    private val isSingCheck = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAutoHsvRuleListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding, mSelectList, isSingCheck)
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
        val binding: ItemAutoHsvRuleListBinding,
        val selectList: MutableList<IAutoRulePoint>,
        val isSingCheck: Boolean,
    ) : RecyclerView.ViewHolder(binding.root) {
        private var mIAutoRulePoint: IAutoRulePoint? = null

        init {
            binding.cBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isSingCheck){

                }else{
                    mIAutoRulePoint?.setIsSelected(isChecked)
                }
            }
        }

        fun bindData(item: IAutoRulePoint) {

        }

    }


}