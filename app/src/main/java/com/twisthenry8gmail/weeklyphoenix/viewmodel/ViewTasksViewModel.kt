package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import com.twisthenry8gmail.weeklyphoenix.DiffLiveData
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.tasks.Task
import com.twisthenry8gmail.weeklyphoenix.data.tasks.TaskRepository
import com.twisthenry8gmail.weeklyphoenix.util.TaskComparator
import com.twisthenry8gmail.weeklyphoenix.util.bundles.TaskDateBundle
import com.twisthenry8gmail.weeklyphoenix.util.TasksDiff
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel
import kotlinx.coroutines.launch

class ViewTasksViewModel(private val args: Bundle?, private val tasksRepository: TaskRepository) :
    NavigatorViewModel() {

    val date = TaskDateBundle.extractDate(args)

    val diffData =
        object : DiffLiveData<Task, Task>(
            tasksRepository.getAllFor(date),
            viewModelScope
        ) {

            val tasksComparator = TaskComparator()

            override fun getCallback(oldData: List<Task>, newData: List<Task>): DiffUtil.Callback {

                return TasksDiff(
                    oldData,
                    newData
                )
            }

            override fun map(data: List<Task>): List<Task> {

                return data.sortedWith(tasksComparator)
            }
        }

    fun onClose() {

        navigateBack()
    }

    fun onAddTask() {

        navigateTo(R.id.action_global_fragmentAddTask, TaskDateBundle(date))
    }

    fun onTaskCompleteChanged(task: Task, complete: Boolean) {

        viewModelScope.launch {

            tasksRepository.setComplete(task.id, complete)
        }
    }

    class Factory(private val args: Bundle?, private val tasksRepository: TaskRepository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(ViewTasksViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return ViewTasksViewModel(args, tasksRepository) as T
            }

            throw IllegalArgumentException()
        }
    }
}