package com.twisthenry8gmail.weeklyphoenix.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.OnRebindCallback
import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.databinding.GoalCard2Binding
import com.twisthenry8gmail.weeklyphoenix.util.GoalDisplayUtil

class GoalAdapter2 : RecyclerView.Adapter<GoalAdapter2.VH>() {

    var goals = listOf<Goal>()
    var clickHandler: ClickHandler? = null

    override fun getItemCount(): Int {

        return goals.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val binding = GoalCard2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(
        holder: VH,
        position: Int,
        payloads: MutableList<Any>
    ) {

        if (payloads.isEmpty()) {

            super.onBindViewHolder(holder, position, payloads)
        } else {

            payloads.filterIsInstance<Change>().forEach {

                when (it) {

                    Change.PROGRESS -> {

                        val goal = goals[position]

                        // TODO Better method to stop the rebind? - STACK
                        val callback = object : OnRebindCallback<GoalCard2Binding>() {

                            override fun onPreBind(binding: GoalCard2Binding?): Boolean {

                                binding?.goalCardProgress?.updateProgress(goal, true)
                                binding?.goalCardProgressText?.text =
                                    GoalDisplayUtil.displayProgressToTarget(
                                        holder.itemView.context.resources,
                                        goal
                                    )
                                binding?.removeOnRebindCallback(this)

                                return false
                            }
                        }

                        holder.binding.addOnRebindCallback(callback)
                        holder.binding.goal = goal
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val goal = goals[position]
        holder.bind(goal, clickHandler)
    }

    class VH(val binding: GoalCard2Binding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: Goal, clickHandler: ClickHandler?) {

            binding.goal = goal
            binding.clickhandler = clickHandler
            binding.executePendingBindings()
        }
    }

    enum class Change {

        PROGRESS
    }

    interface ClickHandler {

        fun onGoalAction(goal: Goal, card: View)

        fun onGoalClick(goal: Goal)
    }
}