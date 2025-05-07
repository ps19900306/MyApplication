package com.example.myapplication.auto_hsv_rule.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemAutoHsvRuleListBinding
import com.nwq.baseutils.singleClick
import com.nwq.callback.CallBack
import com.nwq.opencv.IAutoRulePoint


class AutoHsvRuleAdapter(val isSingCheck:Boolean = false) : RecyclerView.Adapter<AutoHsvRuleAdapter.ViewHolder>() {

    private val list = mutableListOf<IAutoRulePoint>()
    private var lastSelectPoint = -1;
    private var itemClickListener:CallBack<IAutoRulePoint?>?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAutoHsvRuleListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    fun getSelectList(): List<IAutoRulePoint> {
        return list.filter { it.getIsSelected() }
    }


    fun setItemClickListener(itemClickListener:CallBack<IAutoRulePoint?>){
        this.itemClickListener = itemClickListener
    }

    fun updateData(list: List<IAutoRulePoint>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
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
    ) : RecyclerView.ViewHolder(binding.root) {
        private var mIAutoRulePoint: IAutoRulePoint? = null

        init {
            binding.cBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isSingCheck){
                    if (lastSelectPoint==bindingAdapterPosition){
                        mIAutoRulePoint?.setIsSelected(isChecked)
                    }else{
                        list.getOrNull(lastSelectPoint)?.let {
                            it.setIsSelected(false)
                            notifyItemChanged(lastSelectPoint)
                        }
                        mIAutoRulePoint?.setIsSelected(true)
                        lastSelectPoint=bindingAdapterPosition
                    }
                }else{
                    mIAutoRulePoint?.setIsSelected(isChecked)
                }
            }

            binding.root.singleClick {
                itemClickListener?.onCallBack(mIAutoRulePoint)
            }


        }

        fun bindData(item: IAutoRulePoint) {
            binding.cBox.isChecked = item.getIsSelected()
            binding.tv.text = item.getTag()
            item.getStandardBitmap()?.let {
                binding.srcImg.setImageBitmap(it)
            }
        }

    }


}