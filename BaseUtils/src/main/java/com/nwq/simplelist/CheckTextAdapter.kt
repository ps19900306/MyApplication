package com.nwq.simplelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nwq.baseutils.R
import com.nwq.baseutils.singleClick
import com.nwq.callback.CallBack

/**
 * 一个通用的 RecyclerView 适配器，用于显示实现了 [IText] 接口的数据列表。
 *
 * @param T 数据类型，需实现 [IText] 接口
 * @property list 数据源列表，包含多个 [IText<T>] 对象
 * @property mCallBack 点击回调接口，用于通知外部点击事件
 * @property layoutId 列表项布局资源 ID，默认为 [R.layout.item_text]
 * @property textId 显示文本的 TextView 的资源 ID，默认为 [R.id.textTv]
 * @property normalBgRes 默认状态下的背景资源，默认为 [R.drawable.bg_item_normal]
 * @property selectBgRes 选中状态下的背景资源，默认为 [R.drawable.bg_item_select]
 */
class CheckTextAdapter<T>(
    val list: List<ICheckText<T>>,
    private val layoutId: Int = R.layout.item_text,
    private val textId: Int = R.id.textTv,
    private val normalBgRes: Int = R.drawable.bg_item_normal,
    private val selectBgRes: Int = R.drawable.bg_item_select
) : RecyclerView.Adapter<CheckTextAdapter<T>.ViewHolder>() {


    public fun getSelectedItem(): List<ICheckText<T>> {
        return list.filter { it.isCheckStatus() }
    }

    /**
     * 创建并返回一个新的 ViewHolder 实例。
     *
     * @param parent 父容器 ViewGroup
     * @param viewType 视图类型（未使用）
     * @return 新创建的 ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(layoutId, parent, false),
            textId
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
    }


    /**
     * ViewHolder 用于缓存列表项视图及其数据。
     */
    inner class ViewHolder(view: View, textId: Int) :
        RecyclerView.ViewHolder(view) {

        private val mTextView: TextView
        private var mIKeyText: ICheckText<T>? = null

        init {
            // 设置单击事件监听器
            itemView.singleClick {
                mIKeyText?.apply {
                    setCheckStatus(!isCheckStatus())
                    if (isCheckStatus()) {
                        itemView.rootView.setBackgroundResource(selectBgRes)
                    } else {
                        itemView.rootView.setBackgroundResource(normalBgRes)
                    }
                }
            }
            mTextView = itemView.findViewById(textId) // 初始化 TextView
        }

        /**
         * 绑定数据到 ViewHolder，并设置背景状态。
         *
         * @param item 要绑定的数据对象
         * @param p 数据对象的位置索引
         */
        fun bindData(item: ICheckText<T>) {
            mIKeyText = item
            mTextView.text = item.getText() // 设置文本内容
            if (item.isCheckStatus()) {
                itemView.rootView.setBackgroundResource(selectBgRes)
            } else {
                itemView.rootView.setBackgroundResource(normalBgRes)
            }
        }
    }


}