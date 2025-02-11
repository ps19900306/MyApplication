package com.example.myapplication.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemAutoHsvRuleDetailBinding

import com.nwq.baseutils.singleClick
import com.nwq.callback.CallBack
import com.nwq.opencv.hsv.HSVRule



//
class HsvRuleAdapter(val isSingCheck: Boolean = false) :
    RecyclerView.Adapter<HsvRuleAdapter.ViewHolder>() {

    private val TAG = "HsvRuleAdapter"
    private val list = mutableListOf<HSVRule>()
    private var lastSelectPoint = -1;
    private var itemClickListener: CallBack<HSVRule?>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i(TAG, "onCreateViewHolder: ")
        val binding = ItemAutoHsvRuleDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    fun getSelectList(): List<HSVRule> {
        return list.filter { it.getIsSelected() }
    }

    fun setItemClickListener(itemClickListener: CallBack<HSVRule?>) {
        this.itemClickListener = itemClickListener
    }

    fun updateData(list: List<HSVRule>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        Log.i(TAG, "getItemCount: ${list.size}")
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list.getOrNull(position)?.let {
            holder.bindData(it)
        }
    }


    inner class ViewHolder(
        val binding: ItemAutoHsvRuleDetailBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        private var mIAutoRulePoint: HSVRule? = null

        init {
            binding.cBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isSingCheck) {
                    if (lastSelectPoint == bindingAdapterPosition) {
                        mIAutoRulePoint?.setIsSelected(isChecked)
                    } else {
                        list.getOrNull(lastSelectPoint)?.let {
                            it.setIsSelected(false)
                            notifyItemChanged(lastSelectPoint)
                        }
                        mIAutoRulePoint?.setIsSelected(true)
                        lastSelectPoint = bindingAdapterPosition
                    }
                } else {
                    mIAutoRulePoint?.setIsSelected(isChecked)
                }
            }

            binding.tv.singleClick {
                itemClickListener?.onCallBack(mIAutoRulePoint)
            }
        }

        fun bindData(item: HSVRule) {
            with(item){
                mIAutoRulePoint = this
                binding.cBox.isChecked = getIsSelected()
                binding.tv.text = item.toString()
                //binding.colorList.adapter = ColorAdapter(HsvRuleUtils.getColorsList(minH, maxH, minS, maxS, minV, maxV))
            }
        }

    }


}