package com.nwq.simplelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nwq.baseutils.R
import com.nwq.baseutils.singleClick
import com.nwq.callback.CallBack
import com.nwq.callback.CallBack2
import com.nwq.callback.CommonCallBack2

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
class TextAdapter<T>(
    val list: MutableList<IText<T>> = mutableListOf(),
    private val layoutId: Int = R.layout.item_text,
    private val textId: Int = R.id.textTv,
    private val normalBgRes: Int = R.drawable.bg_item_normal,
    private val selectBgRes: Int = R.drawable.bg_item_select,
    val mCallBack: CallBack<T>? = null,
) : RecyclerView.Adapter<TextAdapter<T>.ViewHolder>() {





    public fun upData(list: List<ICheckText<T>>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * 当前选中的 item 的位置索引，默认值为 -1 表示无选中项
     */
    private var mSelectPosition: Int = -1

    //如果需要处理自己的一些特有UI和逻辑
    private var bindView: CallBack2<View, T>? = null

    fun setBindView(bindView: CallBack2<View, T>) {
        this.bindView = bindView
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
        holder.bindData(list[position], position)
    }


    /**
     * ViewHolder 用于缓存列表项视图及其数据。
     */
    inner class ViewHolder(view: View, textId: Int) :
        RecyclerView.ViewHolder(view) {

        private val mTextView: TextView
        private var mIKeyText: IText<T>? = null
        private var position: Int = 0

        init {
            // 设置单击事件监听器
            itemView.singleClick {
                if (mSelectPosition != position) {
                    val temp = mSelectPosition
                    mSelectPosition = position
                    notifyItemChanged(temp) // 通知旧选中项刷新
                    itemView.rootView.setBackgroundResource(selectBgRes) // 设置当前为选中状态背景
                }

                // 回调选中的数据对象
                mIKeyText?.let {
                    mCallBack?.onCallBack(it.getT())
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
        fun bindData(item: IText<T>, p: Int) {
            mIKeyText = item
            mTextView.text = item.getText() // 设置文本内容
            position = p
            // 根据是否选中设置背景
            if (mSelectPosition == p) {
                itemView.rootView.setBackgroundResource(selectBgRes)
            } else {
                itemView.rootView.setBackgroundResource(normalBgRes)
            }
            bindView?.onCallBack(itemView, item.getT())
        }
    }


}