package com.twisthenry8gmail.weeklyphoenix.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.weeklyphoenix.data.goals.Goal
import com.twisthenry8gmail.weeklyphoenix.databinding.GoalCardAddBinding
import com.twisthenry8gmail.weeklyphoenix.databinding.GoalCardBinding
import java.util.*

class GoalAdapter : RecyclerView.Adapter<GoalViewHolder>(),
    GoalsItemTouchCallback.SortableAdapter {

    var goals = listOf<Goal?>()
    lateinit var listener: Listener

    override fun getItemViewType(position: Int): Int {

        return if (goals[position] == null) TYPE_ADD else TYPE_GOAL
    }

    override fun getItemCount(): Int {

        return goals.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        return if (viewType == TYPE_GOAL) {

            GoalViewHolder.Content(GoalCardBinding.inflate(layoutInflater, parent, false))
        } else {

            GoalViewHolder.Add(GoalCardAddBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {

        when (holder) {

            is GoalViewHolder.Content -> {

                val goal = goals[position]!!
                holder.bind(goal, listener)
            }

            is GoalViewHolder.Add -> {

                holder.bind(listener)
            }
        }
    }

    override fun swapData(position1: Int, position2: Int) {

        Collections.swap(goals, position1, position2)
    }

    override fun isSortable(position: Int): Boolean {

        return position < goals.size - 1
    }

    override fun onDrop(position: Int) {

        listener.onGoalMove(goals[position]!!, position)
    }

    companion object {

        const val TYPE_GOAL = 0
        const val TYPE_ADD = 1
    }

    interface Listener {

        fun onGoalClick(goal: Goal)

        fun onGoalAdd()

        fun onGoalMove(goal: Goal, newSortOrder: Int)
    }
}