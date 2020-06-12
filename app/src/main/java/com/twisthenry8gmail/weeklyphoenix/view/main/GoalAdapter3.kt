package com.twisthenry8gmail.weeklyphoenix.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.databinding.GoalCard3Binding
import com.twisthenry8gmail.weeklyphoenix.databinding.GoalCardAddBinding

class GoalAdapter3 : RecyclerView.Adapter<GoalAdapter3.VH>() {

    var goals = listOf<Goal?>(null)
    lateinit var clickHandler: ClickHandler

    override fun getItemViewType(position: Int): Int {

        return if (goals[position] == null) TYPE_ADD else TYPE_GOAL
    }

    override fun getItemCount(): Int {

        return goals.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val layoutInflater = LayoutInflater.from(parent.context)

        return if (viewType == TYPE_GOAL) {

            VH.Goal(GoalCard3Binding.inflate(layoutInflater, parent, false))
        } else {

            VH.GoalAdd(GoalCardAddBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        when (holder) {

            is VH.Goal -> {

                val goal = goals[position]!!
                holder.bind(goal, clickHandler)
            }

            is VH.GoalAdd -> {

                holder.bind(clickHandler)
            }
        }
    }

    companion object {

        const val TYPE_GOAL = 0
        const val TYPE_ADD = 1
    }

    sealed class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        class Goal(val binding: GoalCard3Binding) : VH(binding.root) {

            fun bind(
                goal: com.twisthenry8gmail.weeklyphoenix.data.Goal,
                clickHandler: ClickHandler
            ) {

                binding.goal = goal
                binding.clickhandler = clickHandler
                binding.executePendingBindings()
            }
        }

        class GoalAdd(val binding: GoalCardAddBinding) : VH(binding.root) {

            fun bind(clickHandler: ClickHandler) {

                binding.clickhandler = clickHandler
                binding.executePendingBindings()
            }
        }
    }

    interface ClickHandler {

        fun onGoalClick(goal: Goal)

        fun onGoalAdd()
    }
}