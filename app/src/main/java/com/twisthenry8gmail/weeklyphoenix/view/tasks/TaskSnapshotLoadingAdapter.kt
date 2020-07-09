package com.twisthenry8gmail.weeklyphoenix.view.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import com.twisthenry8gmail.recyclerextensions.StatefulRecyclerHelper
import com.twisthenry8gmail.weeklyphoenix.databinding.TaskSnapshotCardBinding
import com.twisthenry8gmail.weeklyphoenix.view.tasks.TaskSnapshotViewHolder

class TaskSnapshotLoadingAdapter : StatefulRecyclerHelper.LoadingAdapter<TaskSnapshotViewHolder>() {

    override fun getItemCount(): Int {
        return 7
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskSnapshotViewHolder {

        val binding = TaskSnapshotCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskSnapshotViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: TaskSnapshotViewHolder, position: Int) {

        holder.bindLoading()
    }
}