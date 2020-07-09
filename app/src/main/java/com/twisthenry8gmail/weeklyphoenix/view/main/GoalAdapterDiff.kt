package com.twisthenry8gmail.weeklyphoenix.view.main

import androidx.recyclerview.widget.DiffUtil
import com.twisthenry8gmail.weeklyphoenix.data.goals.Goal
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalSnapshot

class GoalAdapterDiff(
    private val oldItems: List<GoalSnapshot?>,
    private val newItems: List<GoalSnapshot?>
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

        return if (oldItem == null || newItem == null) {

            oldItem == newItem
        } else {

            oldItem.title == newItem.title && oldItem.progress == newItem.progress
        }
    }
}