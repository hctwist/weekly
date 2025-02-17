package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.twisthenry8gmail.weeklyphoenix.NonNullLiveData
import com.twisthenry8gmail.weeklyphoenix.NonNullMutableLiveData
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.tasks.Task
import com.twisthenry8gmail.weeklyphoenix.data.tasks.TaskRepository
import com.twisthenry8gmail.weeklyphoenix.util.bundles.TaskDateBundle
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class AddTaskViewModel(
    private val androidResources: Resources,
    private val args: Bundle?,
    private val tasksRepository: TaskRepository
) :
    NavigatorViewModel() {

    val date = TaskDateBundle.extractDate(args)

    val hint = androidResources.getStringArray(R.array.task_suggested_titles).random()

    var title = ""
        set(value) {

            field = value
            updateCanAdd()
        }

    private val _canAdd = NonNullMutableLiveData(false)
    val canAdd: NonNullLiveData<Boolean>
        get() = _canAdd

    fun onBack() {

        navigateBack()
    }

    private fun updateCanAdd() {

        _canAdd.value = resolveTitle().isNotEmpty()
    }

    private fun resolveTitle(): String {

        return title.trim()
    }

    fun onAdd() {

        viewModelScope.launch {

            tasksRepository.add(Task(0, System.currentTimeMillis(), resolveTitle(), date, false))
            navigateBack()
        }
    }

    class Factory(
        private val androidResources: Resources,
        private val args: Bundle?,
        private val tasksRepository: TaskRepository
    ) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(AddTaskViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return AddTaskViewModel(androidResources, args, tasksRepository) as T
            }

            throw IllegalArgumentException()
        }
    }
}