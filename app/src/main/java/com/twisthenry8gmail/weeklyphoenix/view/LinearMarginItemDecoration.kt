package com.twisthenry8gmail.weeklyphoenix.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.weeklyphoenix.R
import kotlin.math.roundToInt

class LinearMarginItemDecoration(private val margin: Float) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        val layoutManager = parent.layoutManager

        if (layoutManager is LinearLayoutManager) {

            val adapterPosition = parent.getChildAdapterPosition(view)

            val margin1 = if (adapterPosition == 0) 0F else margin / 2
            val margin2 = if (adapterPosition == state.itemCount - 1) 0F else margin / 2

            val rtl = parent.layoutDirection == View.LAYOUT_DIRECTION_RTL
            val marginStart = (if (rtl) margin2 else margin1).roundToInt()
            val marginEnd = (if (rtl) margin1 else margin2).roundToInt()

            if (layoutManager.orientation == LinearLayoutManager.HORIZONTAL) {

                outRect.left = marginStart
                outRect.right = marginEnd
            } else {

                outRect.top = marginStart
                outRect.bottom = marginEnd
            }
        } else {

            throw RuntimeException("This item decoration can only be used with a LinearLayoutManager")
        }
    }
}