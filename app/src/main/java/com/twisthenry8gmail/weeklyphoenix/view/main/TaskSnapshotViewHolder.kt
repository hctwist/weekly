package com.twisthenry8gmail.weeklyphoenix.view.main

import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.weeklyphoenix.data.tasks.TaskSnapshot
import com.twisthenry8gmail.weeklyphoenix.databinding.TaskSnapshotCardBinding

class TaskSnapshotViewHolder(private val binding: TaskSnapshotCardBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(taskSnapshot: TaskSnapshot, clickHandler: TaskSnapshotAdapter.ClickHandler) {

        binding.loading = false
        binding.tasksnapshot = taskSnapshot
        binding.clickhandler = clickHandler
        binding.executePendingBindings()
    }

    fun bindLoading() {

        binding.loading = true
        binding.executePendingBindings()
    }
}