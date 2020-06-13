package com.twisthenry8gmail.weeklyphoenix.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.weeklyphoenix.data.Task
import com.twisthenry8gmail.weeklyphoenix.databinding.TaskRowBinding

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.VH>() {

    var tasks = listOf<Task>()

    lateinit var clickHandler: ClickHandler

    override fun getItemCount(): Int {

        return tasks.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        return VH(TaskRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.bind(tasks[position], clickHandler)
    }

    class VH(val binding: TaskRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task, clickHandler: ClickHandler) {

            binding.task = task
            binding.clickhandler = clickHandler
            binding.executePendingBindings()
        }
    }

    interface ClickHandler {

        fun onToggleTaskComplete(task: Task)
    }
}