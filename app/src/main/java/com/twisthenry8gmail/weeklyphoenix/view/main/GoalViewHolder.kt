package com.twisthenry8gmail.weeklyphoenix.view.main

import android.os.Handler
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalSnapshot
import com.twisthenry8gmail.weeklyphoenix.databinding.GoalCardAddBinding
import com.twisthenry8gmail.weeklyphoenix.databinding.GoalCardBinding

sealed class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    class Content(val binding: GoalCardBinding) : GoalViewHolder(binding.root) {

        fun bind(
            goalSnapshot: GoalSnapshot,
            listener: GoalAdapter.Listener
        ) {

            binding.loading = false
            binding.goalsnapshot = goalSnapshot
            binding.listener = listener
            binding.executePendingBindings()
        }

        fun bindLoading() {

            binding.loading = true
            binding.executePendingBindings()
        }
    }

    class Add(val binding: GoalCardAddBinding) : GoalViewHolder(binding.root) {

        fun bind(listener: GoalAdapter.Listener) {

            binding.listener = listener
            binding.executePendingBindings()
        }
    }
}