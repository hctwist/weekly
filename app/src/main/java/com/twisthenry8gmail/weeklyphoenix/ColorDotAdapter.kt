package com.twisthenry8gmail.weeklyphoenix

import android.content.res.ColorStateList
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView

class ColorDotAdapter(val colors: Array<Int>) : RecyclerView.Adapter<ColorDotAdapter.VH>() {

    override fun getItemCount(): Int {

        return colors.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        return VH(LayoutInflater.from(parent.context).inflate(R.layout.color_dot, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.dot.backgroundTintList = ColorStateList.valueOf(colors[position])
        holder.dot.tag = colors[position]
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dot = itemView as ImageButton
    }
}