package com.nwq.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nwq.baseutils.R
import com.nwq.baseutils.singleClick
import com.nwq.callback.CallBack

class KeyTextAdapter(
    val list: List<IKeyText>,
    val mCallBack: CallBack<Int>? = null,
    private val layoutId: Int = R.layout.item_text,
    private val textId: Int = R.id.textTv,
) :
    RecyclerView.Adapter<KeyTextAdapter.ViewHolder>() {


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


    inner class ViewHolder(view: View, textId: Int) :
        RecyclerView.ViewHolder(view) {

        private val mTextView: TextView
        private var mIKeyText: IKeyText? = null

        init {
            itemView.singleClick {
                mIKeyText?.let {
                    mCallBack?.onCallBack(it.getKey())
                }
            }
            mTextView = itemView.findViewById(textId)
        }

        fun bindData(item: IKeyText) {
            mIKeyText = item
            mTextView.text = item.getText()
        }
    }

}