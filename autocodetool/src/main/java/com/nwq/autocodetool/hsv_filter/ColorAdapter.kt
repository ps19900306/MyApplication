package com.nwq.autocodetool.hsv_filter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nwq.autocodetool.R
import com.nwq.data.ColorItem

class ColorAdapter(private val colorList: List<ColorItem>) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorTv: TextView = itemView.findViewById(R.id.colorTv)
        val colorV: View = itemView.findViewById(R.id.colorV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_colors, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val colorItem = colorList[position]
        val color = Color.HSVToColor(floatArrayOf(colorItem.hsv[0]*1.99f,colorItem.hsv[1]/255,colorItem.hsv[2]/255))
        holder.colorTv.text = "${colorItem.hsv[0].toInt()}:${colorItem.hsv[1].toInt()}:${colorItem.hsv[2].toInt()}"
        holder.colorV.setBackgroundColor(color)
    }

    override fun getItemCount(): Int {
        return colorList.size
    }
}