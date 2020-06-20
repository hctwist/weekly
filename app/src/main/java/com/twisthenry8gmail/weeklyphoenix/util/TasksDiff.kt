package com.twisthenry8gmail.weeklyphoenix.util

import androidx.recyclerview.widget.DiffUtil
import com.twisthenry8gmail.weeklyphoenix.data.tasks.Task
import com.twisthenry8gmail.weeklyphoenix.view.viewtasks.TaskAdapter

class TasksDiff(
    private val oldItems: List<Task>,
    private val newItems: List<Task>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {

        return oldItems.size
    }

    override fun getNewListSize(): Int {

        return newItems.size
    }


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        return oldItems[oldItemPosition].id == newItems[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        return oldItems[oldItemPosition] == newItems[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {

        if (oldItems[oldItemPosition].complete != newItems[newItemPosition].complete) {

            return TaskAdapter.Change.COMPLETE
        }

        return null
    }
}