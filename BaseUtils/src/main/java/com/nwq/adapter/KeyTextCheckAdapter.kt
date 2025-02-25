package com.nwq.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.nwq.baseutils.R
import com.nwq.baseutils.singleClick
import com.nwq.callback.CallBack
import kotlin.math.log

/**
 * 通用的Key-Text-Check适配器
 * @param list 数据列表，必须实现 [CheckKeyText] 接口
 * @param mCallBack 回调，传递选中项的 Key
 * @param layoutId 自定义布局资源 ID，默认为 R.layout.item_check_text
 * @param textId CheckBox 的资源 ID，默认为 R.id.textCb
 * @param isSingle 是否为单选模式，默认为 false (多选)
 */
class KeyTextCheckAdapter(
    private val list: List<CheckKeyText>,
    private val mCallBack: CallBack<Int>? = null,
    private val layoutId: Int = R.layout.item_check_text,
    private val textId: Int = R.id.textCb,
    private val isSingle: Boolean = false
) : RecyclerView.Adapter<KeyTextCheckAdapter.ViewHolder>() {

    // 记录单选模式下的选中项位置
    private var selectedPosition: Int = if (isSingle)0 else -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)

        return ViewHolder(view, textId)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getSelectedItem(): List<CheckKeyText> {
        return if (isSingle) {
            val d = mutableListOf<CheckKeyText>();
            list.getOrNull(selectedPosition)?.let {
                d.add(it)
            }
            d
        } else {
            list.filter { it.isChecked }
        }

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position], position)
    }

    inner class ViewHolder(view: View, textId: Int) : RecyclerView.ViewHolder(view) {

        private val checkBox: CheckBox = itemView.findViewById(textId)
        private var currentItem: CheckKeyText? = null

        init {
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isSingle ) {
                    if (isChecked){
                        if (selectedPosition != adapterPosition) {
                            val previousPosition = selectedPosition
                            selectedPosition = adapterPosition
                            Log.i("KeyTextCheckAdapter", "onCheckedChanged: $previousPosition")
                            if (previousPosition != -1)
                            {
                                notifyItemChanged(previousPosition)
                            }
                        }
                    }else{
                        if (selectedPosition == adapterPosition){
                            checkBox.isChecked = true
                        }
                    }
                }else{
                    currentItem?.isChecked = isChecked
                }
            }

//            if (textId == itemView.id) {
//
//            } else {
//                // 设置单击事件
//                itemView.singleClick {
//                    currentItem?.let { item ->
//                        if (isSingle) {
//                            // 单选模式处理
//                            if (selectedPosition != adapterPosition) {
//                                val previousPosition = selectedPosition
//                                selectedPosition = adapterPosition
//                                notifyItemChanged(previousPosition) // 更新之前选中的项
//                                notifyItemChanged(selectedPosition) // 更新当前选中的项
//                                mCallBack?.onCallBack(item.getKey())
//                            }
//                        } else {
//                            // 多选模式切换选中状态
//                            item.isChecked = !item.isChecked
//                            notifyItemChanged(adapterPosition)
//                            mCallBack?.onCallBack(item.getKey())
//                        }
//
//                    }
//                }
//            }


            // 设置 CheckBox 的点击事件（防止与 itemView 点击冲突）

        }

        /**
         * 绑定数据到 ViewHolder
         * @param item 数据项
         * @param position 当前项的位置
         */
        fun bindData(item: CheckKeyText, position: Int) {
            currentItem = item
            checkBox.text = item.getText()
            checkBox.isChecked = if (isSingle) {
                // 单选模式下，只有选中的项为 true
                position == selectedPosition
            } else {
                // 多选模式下，从数据项中获取状态
                item.isChecked
            }
        }
    }
}


