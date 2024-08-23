package com.nwq.loguitls.db

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nwq.loguitls.R

class LogListAdapter : PagingDataAdapter<LogEntity, LogListAdapter.LogViewHolder>(LOG_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = getItem(position)
        if (log != null) {
            holder.bind(log)
        }
    }

    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTag: TextView = itemView.findViewById(R.id.textViewTag)
        private val textViewLevel: TextView = itemView.findViewById(R.id.textViewLevel)
        private val textViewMessage: TextView = itemView.findViewById(R.id.textViewMessage)
        private val textViewTimestamp: TextView = itemView.findViewById(R.id.textViewTimestamp)


        fun bind(log: LogEntity) {
            textViewTag.text = "Tag: ${log.tag}"
            textViewLevel.text = "Level: ${log.level}"
            textViewMessage.text = "Message: ${log.msg}"
            textViewTimestamp.text = "Timestamp: ${log.recordTime}"
        }
    }

    companion object {
        private val LOG_COMPARATOR = object : DiffUtil.ItemCallback<LogEntity>() {
            override fun areItemsTheSame(oldItem: LogEntity, newItem: LogEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: LogEntity, newItem: LogEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}