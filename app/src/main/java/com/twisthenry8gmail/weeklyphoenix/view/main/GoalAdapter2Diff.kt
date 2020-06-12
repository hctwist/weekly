package com.twisthenry8gmail.weeklyphoenix.view.main

import androidx.recyclerview.widget.DiffUtil
import com.twisthenry8gmail.weeklyphoenix.data.Goal

class GoalAdapter2Diff(
    private val oldItems: List<Goal?>,
    private val newItems: List<Goal?>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {

        return oldItems.size
    }

    override fun getNewListSize(): Int {

        return newItems.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        return oldItem?.id == newItem?.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        return oldItem == newItem
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {

        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        if (oldItem!!.progress != newItem!!.progress) {

            return GoalAdapter2.Change.PROGRESS
        }

        return null
    }
}