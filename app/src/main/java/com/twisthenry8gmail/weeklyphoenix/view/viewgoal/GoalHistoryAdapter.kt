package com.twisthenry8gmail.weeklyphoenix.view.viewgoal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.weeklyphoenix.data.goals.Goal
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalHistory
import com.twisthenry8gmail.weeklyphoenix.databinding.GoalHistoryRowBinding

class GoalHistoryAdapter : RecyclerView.Adapter<GoalHistoryAdapter.VH>() {

    lateinit var goal: Goal
    var histories = listOf<GoalHistory>()

    override fun getItemCount(): Int {

        return histories.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val inflater = LayoutInflater.from(parent.context)
        return VH(GoalHistoryRowBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.bind(goal, histories[position])
    }

    class VH(val binding: GoalHistoryRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: Goal, history: GoalHistory) {

            binding.goal = goal
            binding.goalhistory = history
            binding.executePendingBindings()
        }
    }
}