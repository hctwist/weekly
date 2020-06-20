package com.twisthenry8gmail.weeklyphoenix.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.weeklyphoenix.data.tasks.TaskSnapshot
import com.twisthenry8gmail.weeklyphoenix.databinding.TaskSnapshotCardBinding

class TaskSnapshotAdapter : RecyclerView.Adapter<TaskSnapshotViewHolder>() {

    var taskSnapshots = listOf<TaskSnapshot>()
    lateinit var clickHandler: ClickHandler

    override fun getItemCount(): Int {

        return taskSnapshots.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskSnapshotViewHolder {

        val binding =
            TaskSnapshotCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskSnapshotViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: TaskSnapshotViewHolder, position: Int) {

        holder.bind(taskSnapshots[position], clickHandler)
    }

    interface ClickHandler {

        fun onCardClick(taskSnapshot: TaskSnapshot)

        fun onAdd(taskSnapshot: TaskSnapshot)
    }
}