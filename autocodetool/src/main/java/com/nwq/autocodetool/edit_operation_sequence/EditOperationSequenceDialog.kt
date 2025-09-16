package com.nwq.autocodetool.edit_operation_sequence

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nwq.autocodetool.databinding.DialogEditOperationSequenceBinding
import com.nwq.base.BaseDialogFragment
import com.nwq.callback.CallBack
import com.nwq.optlib.MatResult

class EditOperationSequenceDialog : BaseDialogFragment<DialogEditOperationSequenceBinding>() {
    private lateinit var operationAdapter: OperationAdapter
    private var operations: MutableList<MatResult> = mutableListOf()
    private var callback: CallBack<List<MatResult>>? = null
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogEditOperationSequenceBinding {
        return DialogEditOperationSequenceBinding.inflate(inflater, container, false)
    }

    fun setOperations(operations: List<MatResult>): EditOperationSequenceDialog {
        this.operations.clear()
        this.operations.addAll(operations)
        return this
    }

    fun setCallback(callback: CallBack<List<MatResult>>): EditOperationSequenceDialog {
        this.callback = callback
        return this
    }

    override fun initData() {
        setupRecyclerView()
        setupButtons()
    }

    private fun setupRecyclerView() {
        operationAdapter = OperationAdapter(operations.toMutableList()) { viewHolder ->
            // 启动拖拽
            itemTouchHelper.startDrag(viewHolder)
        }
        binding.operationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.operationRecyclerView.adapter = operationAdapter

        itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, 0)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                operationAdapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 不支持滑动删除
            }

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return false
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.operationRecyclerView)

        // 设置拖拽监听
        operationAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                // 更新操作顺序
            }
        })
    }

    private fun setupButtons() {
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.saveButton.setOnClickListener {
            callback?.onCallBack(operationAdapter.getOperations())
            dismiss()
        }
    }

    // 允许通过拖拽图标进行拖拽
    fun onDragStarted(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }
}
