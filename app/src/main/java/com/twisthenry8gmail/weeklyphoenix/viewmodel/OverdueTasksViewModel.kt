package com.twisthenry8gmail.weeklyphoenix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import com.twisthenry8gmail.weeklyphoenix.DiffLiveData
import com.twisthenry8gmail.weeklyphoenix.data.tasks.Task
import com.twisthenry8gmail.weeklyphoenix.data.tasks.TaskRepository
import com.twisthenry8gmail.weeklyphoenix.util.TasksDiff
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel
import kotlinx.coroutines.launch

class OverdueTasksViewModel(private val tasksRepository: TaskRepository) : NavigatorViewModel() {

    val overdueDiffData =
        object : DiffLiveData<Task, Task>(tasksRepository.getOverdue(), viewModelScope) {

            override fun getCallback(oldData: List<Task>, newData: List<Task>): DiffUtil.Callback {

                return TasksDiff(oldData, newData)
            }

            override fun map(data: List<Task>): List<Task> {

                return data
            }
        }

    fun onClose() {

        navigateBack()
    }

    fun onTaskComplete(task: Task) {

        viewModelScope.launch {

            tasksRepository.setComplete(task.id, true)
        }
    }

    class Factory(private val tasksRepository: TaskRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            @Suppress("UNCHECKED_CAST")
            return OverdueTasksViewModel(tasksRepository) as T
        }
    }
}