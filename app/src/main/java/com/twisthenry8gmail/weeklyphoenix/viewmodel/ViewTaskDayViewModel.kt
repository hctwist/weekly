package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Task
import com.twisthenry8gmail.weeklyphoenix.data.TaskRepository
import com.twisthenry8gmail.weeklyphoenix.util.bundles.TaskDateBundle
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ViewTaskDayViewModel(private val args: Bundle?, private val tasksRepository: TaskRepository) :
    NavigatorViewModel() {

    val date = TaskDateBundle.extractDate(args)
    val taskDay = tasksRepository.getDay(date)

    fun onClose() {

        navigateBack()
    }

    fun onAddTask() {

        navigateTo(R.id.action_fragmentViewTaskDay_to_fragmentAddTask, TaskDateBundle(date))
    }

    fun onToggleTaskComplete(task: Task) {

        viewModelScope.launch {

            tasksRepository.setComplete(task.id, !task.complete)
        }
    }

    class Factory(private val args: Bundle?, private val tasksRepository: TaskRepository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(ViewTaskDayViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return ViewTaskDayViewModel(args, tasksRepository) as T
            }

            throw IllegalArgumentException()
        }
    }
}