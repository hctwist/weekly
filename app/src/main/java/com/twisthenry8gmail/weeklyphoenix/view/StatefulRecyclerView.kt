package com.twisthenry8gmail.weeklyphoenix.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.weeklyphoenix.R

class StatefulRecyclerView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {

    private val observer = object : AdapterDataObserver() {

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {

            setEmpty(false)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {

            setEmpty(isEmpty())
        }
    }

    private val emptyViewId: Int
    private var emptyView: View? = null

    init {

        visibility = View.GONE

        context.obtainStyledAttributes(
            attrs,
            R.styleable.StatefulRecyclerView
        ).run {

            emptyViewId = getResourceId(R.styleable.StatefulRecyclerView_emptyView, 0)
            recycle()
        }
    }

    override fun setAdapter(newAdapter: Adapter<*>?) {

        adapter?.unregisterAdapterDataObserver(observer)
        newAdapter?.registerAdapterDataObserver(observer)

        super.setAdapter(newAdapter)
    }

    fun setEmpty(empty: Boolean) {

        if (emptyView == null) {

            emptyView = rootView.findViewById(emptyViewId)
        }

        if (empty) {

            animateVisibility(View.GONE)
            emptyView?.animateVisibility(View.VISIBLE)
        } else {

            emptyView?.animateVisibility(View.GONE)
            animateVisibility(View.VISIBLE)
        }
    }

    private fun View.animateVisibility(toVisibility: Int) {

        if (visibility == toVisibility) return

        val duration = 200L
        val to = if (toVisibility == View.VISIBLE) 1F else 0F

        animate().alpha(to).setDuration(duration).withStartAction {

            visibility = toVisibility
        }.start()
    }

    fun isEmpty(): Boolean {

        return adapter?.itemCount ?: 0 == 0
    }
}