package com.example.myapplication.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemAutoHsvRuleDetailBinding
import com.example.myapplication.databinding.ItemAutoHsvRuleListBinding
import com.nwq.baseutils.singleClick
import com.nwq.callback.CallBack
import com.nwq.opencv.IAutoRulePoint
import com.nwq.opencv.hsv.HSVRule


class HsvRuleAdapter(val isSingCheck: Boolean = false) :
    RecyclerView.Adapter<HsvRuleAdapter.ViewHolder>() {

    private val list = mutableListOf<HSVRule>()
    private var lastSelectPoint = -1;
    private var itemClickListener: CallBack<IAutoRulePoint?>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAutoHsvRuleDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    fun setItemClickListener(itemClickListener: CallBack<IAutoRulePoint?>) {
        this.itemClickListener = itemClickListener
    }

    fun updateData(list: List<HSVRule>) {
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
        val binding: ItemAutoHsvRuleDetailBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        private var mIAutoRulePoint: IAutoRulePoint? = null
        private var mColorAdapter: ColorAdapter? = null

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

            binding.root.singleClick {
                itemClickListener?.onCallBack(mIAutoRulePoint)
            }


        }

        fun bindData(item: HSVRule) {
            binding.cBox.isChecked = item.getIsSelected()
//            binding.tv.text = item.getTag()
//            item.getStandardBitmap()?.let {
//                binding.srcImg.setImageBitmap(it)
//            }
        }

    }


}