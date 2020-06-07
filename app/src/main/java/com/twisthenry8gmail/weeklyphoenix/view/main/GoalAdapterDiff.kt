package com.twisthenry8gmail.weeklyphoenix.view.main

import androidx.recyclerview.widget.DiffUtil

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

        return when (oldItem.type) {

            GoalAdapter.Data.Type.HEADER -> true
            GoalAdapter.Data.Type.GOAL, GoalAdapter.Data.Type.GOAL_COMPLETE, GoalAdapter.Data.Type.GOAL_SCHEDULED, GoalAdapter.Data.Type.GOAL_ENDED -> return oldItem.asGoal().id == newItem.asGoal().id
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        return when (oldItem.type) {

            GoalAdapter.Data.Type.HEADER -> oldItem.asHeader() == newItem.asHeader()
            GoalAdapter.Data.Type.GOAL, GoalAdapter.Data.Type.GOAL_COMPLETE, GoalAdapter.Data.Type.GOAL_SCHEDULED, GoalAdapter.Data.Type.GOAL_ENDED -> oldItem.asGoal() == newItem.asGoal()
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
            GoalAdapter.Data.Type.GOAL_COMPLETE, GoalAdapter.Data.Type.GOAL_SCHEDULED, GoalAdapter.Data.Type.GOAL_ENDED -> null
        }
    }
}