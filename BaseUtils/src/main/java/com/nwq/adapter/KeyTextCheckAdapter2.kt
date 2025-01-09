package com.nwq.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nwq.baseutils.R
import com.nwq.baseutils.singleClick
import com.nwq.callback.CallBack

/**
 * 通用的Key-Text-Check适配器
 * @param list 数据列表，必须实现 [CheckKeyText] 接口
 * @param mCallBack 回调，传递选中项的 Key
 * @param layoutId 自定义布局资源 ID，默认为 R.layout.item_check_text
 * @param textId CheckBox 的资源 ID，默认为 R.id.textCb
 */
class KeyTextCheckAdapter2(
    private val list: List<CheckKeyText>,
    private val mCallBack: CallBack<Int>? = null,
    private val layoutId: Int = R.layout.item_text,
    private val textId: Int = R.id.textTv,
) : RecyclerView.Adapter<KeyTextCheckAdapter2.ViewHolder>() {

    // 记录单选模式下的选中项位置
    private var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view, textId)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getSelectedItem(): List<CheckKeyText> {
        val d = mutableListOf<CheckKeyText>()
        list.getOrNull(selectedPosition)?.let {
            d.add(it)
        }
        return d
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position], position)
    }

    inner class ViewHolder(view: View, textId: Int) : RecyclerView.ViewHolder(view) {

        private val checkBox: TextView = itemView.findViewById(textId)
        private var currentItem: CheckKeyText? = null

        init {
            // 设置单击事件
            itemView.singleClick {
                currentItem?.let { item ->
                    if (selectedPosition != adapterPosition) {
                        val previousPosition = selectedPosition
                        selectedPosition = adapterPosition
                        notifyItemChanged(previousPosition) // 更新之前选中的项
                        notifyItemChanged(selectedPosition) // 更新当前选中的项
                        mCallBack?.onCallBack(item.getKey())
                    }

                }
            }

            // 设置 CheckBox 的点击事件（防止与 itemView 点击冲突）
            checkBox.setOnClickListener {
                currentItem?.getKey()?.let { it1 -> mCallBack?.onCallBack(it1) }
            }
        }

        /**
         * 绑定数据到 ViewHolder
         * @param item 数据项
         * @param position 当前项的位置
         */
        fun bindData(item: CheckKeyText, position: Int) {
            currentItem = item
            checkBox.text = item.getText()
            if (item.isChecked) {
                checkBox.setTextColor(itemView.context.getColor(R.color.black))
                checkBox.textSize = 16f
            } else {
                checkBox.setTextColor(itemView.context.getColor(R.color.text_color))
                checkBox.textSize = 12f
            }
        }
    }
}


