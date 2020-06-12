package com.twisthenry8gmail.weeklyphoenix.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.weeklyphoenix.data.TaskDay
import com.twisthenry8gmail.weeklyphoenix.databinding.TaskCardBinding

class TaskDayAdapter : RecyclerView.Adapter<TaskDayAdapter.VH>() {

    var taskDays = listOf<TaskDay>()
    lateinit var clickHandler: ClickHandler

    override fun getItemCount(): Int {

        return taskDays.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val binding = TaskCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.bind(taskDays[position], clickHandler)
    }

    class VH(private val binding: TaskCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(taskDay: TaskDay, clickHandler: ClickHandler) {

            binding.taskday = taskDay
            binding.clickhandler = clickHandler
            binding.executePendingBindings()
        }
    }

    interface ClickHandler {

        fun onTaskDayClick(day: TaskDay)

        fun onAddTask(day: TaskDay)
    }
}