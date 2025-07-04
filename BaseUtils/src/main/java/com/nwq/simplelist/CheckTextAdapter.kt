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
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 一个通用的 RecyclerView 适配器，用于显示实现了 [IText] 接口的数据列表。
 *
 * @param T 数据类型，需实现 [IText] 接口
 * @property mCallBack 点击回调接口，用于通知外部点击事件
 * @property layoutId 列表项布局资源 ID，默认为 [R.layout.item_text]
 * @property textId 显示文本的 TextView 的资源 ID，默认为 [R.id.textTv]
 * @property normalBgRes 默认状态下的背景资源，默认为 [R.drawable.bg_item_normal]
 * @property selectBgRes 选中状态下的背景资源，默认为 [R.drawable.bg_item_select]
 * 单选请使用[TextAdapter]
 */
class CheckTextAdapter<T>(
    private val layoutId: Int = R.layout.item_text,
    private val textId: Int = R.id.textTv,
    private val normalBgRes: Int = R.drawable.bg_item_normal,
    private val selectBgRes: Int = R.drawable.bg_item_select,
    private val mLongClick: CallBack<T>? = null//长按
) : RecyclerView.Adapter<CheckTextAdapter<T>.ViewHolder>() {

    val list: MutableList<ICheckText<T>> = mutableListOf()

    val checkListFlow: MutableStateFlow<List<T>> = MutableStateFlow(listOf())

    //如果需要处理自己的一些特有UI和逻辑
    private var bindView: CallBack2<View, T>? = null

    fun setBindView(bindView: CallBack2<View, T>) {
        this.bindView = bindView
    }

    public fun getSelectedItem(): List<ICheckText<T>> {
        return list.filter { it.isCheckStatus() } ?: listOf()
    }

    public fun removeSelectAndGet(): List<T> {
        val remainingItems = list.filter { !it.isCheckStatus() }
        list.clear()
        list.addAll(remainingItems)
        checkListFlow.tryEmit(listOf())
        notifyDataSetChanged()
        return remainingItems.map { it.getT() }
    }

    public fun removeSelectAndGet2(): List<ICheckText<T>> {
        val remainingItems = list.filter { !it.isCheckStatus() }
        checkListFlow.tryEmit(listOf())
        return remainingItems
    }


    public fun addData(item: ICheckText<T>) {
        list.add(item)
        notifyItemInserted(list.size - 1)
    }

    public fun upData(list: List<ICheckText<T>>) {
        this.list.clear()
        this.list.addAll(list)
        checkListFlow.tryEmit(list.filter { it.isCheckStatus() }.map { it.getT() })
        notifyDataSetChanged()
    }

    //全选 选中
    public fun selectAll(boolean: Boolean) {
        list.forEach { it.setCheckStatus(boolean) }
        checkListFlow.tryEmit(list.map { it.getT() })
        notifyDataSetChanged()
    }

    //反选
    public fun selectReverse() {
        list.forEach {
            it.setCheckStatus(!it.isCheckStatus())
        }
        checkListFlow.tryEmit(list.filter { it.isCheckStatus() }.map { it.getT() })
        notifyDataSetChanged()
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
                        itemView.setBackgroundResource(selectBgRes)
                    } else {
                        itemView.setBackgroundResource(normalBgRes)
                    }
                }
                checkListFlow.tryEmit(list.filter { it.isCheckStatus() }.map { it.getT() })
            }
            mLongClick?.let {
                itemView.setOnLongClickListener {
                    mLongClick.onCallBack(mIKeyText!!.getT())
                    true
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
                itemView.setBackgroundResource(selectBgRes)
            } else {
                itemView.setBackgroundResource(normalBgRes)
            }
            bindView?.onCallBack(itemView, item.getT())
        }
    }


}