package com.twisthenry8gmail.weeklyphoenix.view.main

import androidx.recyclerview.widget.DiffUtil
import com.twisthenry8gmail.weeklyphoenix.view.main.GoalAdapter

class GoalAdapterDiff(
    private val oldItems: List<GoalAdapter.Data>,
    private val newItems: List<GoalAdapter.Data>
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

        if (oldItem.type != newItem.type) {

            return false
        }

        // TODO Maybe not use name as this could change?
        return when (oldItem.type) {

            GoalAdapter.Data.Type.HEADER -> true
            GoalAdapter.Data.Type.GOAL, GoalAdapter.Data.Type.GOAL_COMPLETE -> return oldItem.asGoal().name == newItem.asGoal().name
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        // Here the items must both be goals or complete goals
        return when (oldItem.type) {

            // TODO Only check display fields
            GoalAdapter.Data.Type.HEADER -> true
            GoalAdapter.Data.Type.GOAL, GoalAdapter.Data.Type.GOAL_COMPLETE -> oldItem.asGoal() == newItem.asGoal()
        }

    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {

        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        // Here the items must be of the same type
        return when (oldItem.type) {

            GoalAdapter.Data.Type.HEADER -> null
            GoalAdapter.Data.Type.GOAL -> {

                return if (oldItem.asGoal().progress == newItem.asGoal().progress) {

                    null
                } else {

                    GoalAdapter.Change.PROGRESS
                }
            }
            GoalAdapter.Data.Type.GOAL_COMPLETE -> null
        }
    }
}