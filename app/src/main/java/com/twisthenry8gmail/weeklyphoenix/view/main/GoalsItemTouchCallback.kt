package com.twisthenry8gmail.weeklyphoenix.view.main

import android.animation.ValueAnimator
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.weeklyphoenix.R

class GoalsItemTouchCallback : ItemTouchHelper.Callback() {

    private var targetAdapterPosition = 0

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {

        recyclerView.adapter?.let {

            if (it is SortableAdapter && it.isSortable(viewHolder.adapterPosition)) {

                return makeMovementFlags(ItemTouchHelper.START or ItemTouchHelper.END, 0)
            }
        }

        return 0
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun canDropOver(
        recyclerView: RecyclerView,
        current: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {

        recyclerView.adapter?.let {

            return it is SortableAdapter && it.isSortable(target.adapterPosition)
        }

        return false
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        viewHolder?.itemView?.let {

            // TODO Decide on animation. Slight scale inwards? Slight translation up/down? Elevation?
            it.animate().translationZ(it.context.resources.getDimension(R.dimen.default_elevation))
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {

        val adapter = recyclerView.adapter
        if (adapter is SortableAdapter) {

            val sourcePosition = viewHolder.adapterPosition
            targetAdapterPosition = target.adapterPosition

            // TODO This doesn't really work as the adapter position will not be consistent with the sort order in case of deleted goals etc
            adapter.swapData(sourcePosition, targetAdapterPosition)
            adapter.notifyItemMoved(sourcePosition, targetAdapterPosition)
        }

        return true
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        recyclerView.adapter?.let {

            if (it is SortableAdapter) {

                it.onDrop(viewHolder.adapterPosition)
            }
        }

        viewHolder.itemView.animate().translationZ(0F)
    }

    interface SortableAdapter {

        fun isSortable(position: Int): Boolean
        fun swapData(position1: Int, position2: Int)
        fun onDrop(position: Int)
    }
}