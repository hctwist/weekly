package com.twisthenry8gmail.weeklyphoenix.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.weeklyphoenix.R

class MarginItemDecoration(private val margin: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        val adapterPosition = parent.getChildAdapterPosition(view)

        if (adapterPosition > 0) {

            outRect.top = margin
        }
    }
}