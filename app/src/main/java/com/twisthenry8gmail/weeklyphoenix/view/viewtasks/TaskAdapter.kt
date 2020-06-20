package com.twisthenry8gmail.weeklyphoenix.view.viewtasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.weeklyphoenix.data.tasks.Task
import com.twisthenry8gmail.weeklyphoenix.databinding.TaskRowBinding

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.VH>() {

    var tasks = listOf<Task>()

    lateinit var clickHandler: ClickHandler

    override fun getItemCount(): Int {

        return tasks.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        return VH(
            TaskRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {

        if (payloads.isEmpty()) {

            onBindViewHolder(holder, position)
        } else {

            payloads.filterIsInstance<Change>().forEach {

                when (it) {

                    Change.COMPLETE -> {

                        val task = tasks[position]

                        // TODO Can this be done in goal adapter somehow? It is great!
                        holder.binding.taskRowView.animateChecked(task.complete)
                        holder.bind(task, clickHandler)
                    }
                }
            }
        }
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

        fun onTaskCompleteChanged(task: Task, complete: Boolean)
    }

    enum class Change {

        COMPLETE
    }
}