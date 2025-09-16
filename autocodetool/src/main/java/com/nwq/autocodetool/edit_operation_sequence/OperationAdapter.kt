package com.nwq.autocodetool.edit_operation_sequence

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nwq.autocodetool.R
import com.nwq.optlib.MatResult
import com.nwq.optlib.db.bean.CropAreaDb
import com.nwq.optlib.db.bean.GrayFilterRuleDb
import com.nwq.optlib.db.bean.HsvFilterRuleDb

class OperationAdapter(
    private var operations: MutableList<MatResult>,
    private val onDragStart: ((RecyclerView.ViewHolder) -> Unit)? = null
) : RecyclerView.Adapter<OperationAdapter.ViewHolder>() {

    fun getOperations(): List<MatResult> {
        return operations
    }

    fun updateOperations(newOperations: List<MatResult>) {
        operations.clear()
        operations.addAll(newOperations)
        notifyDataSetChanged()
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                operations[i] = operations[i + 1].also { operations[i + 1] = operations[i] }
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                operations[i] = operations[i - 1].also { operations[i - 1] = operations[i] }
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_operation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val operation = operations[position]
        holder.bind(operation)
    }

    override fun getItemCount(): Int = operations.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val operationNameText: TextView = itemView.findViewById(R.id.operationNameText)
        private val dragHandle: View = itemView.findViewById(R.id.dragHandle)

        init {
            // 设置长按拖拽
            itemView.setOnLongClickListener {
                onDragStart?.invoke(this)
                true
            }

            // 设置拖拽手柄点击拖拽
            dragHandle.setOnClickListener {
                onDragStart?.invoke(this)
            }
        }

        fun bind(operation: MatResult) {
            // 根据操作类型设置显示名称
            val name = when (operation) {
                is CropAreaDb -> "裁剪区域"
                is HsvFilterRuleDb -> "HSV过滤"
                is GrayFilterRuleDb -> "灰度过滤"
                else -> operation.javaClass.simpleName
            }
            operationNameText.text = name
        }

        fun getDragHandle(): View = dragHandle
    }
}
