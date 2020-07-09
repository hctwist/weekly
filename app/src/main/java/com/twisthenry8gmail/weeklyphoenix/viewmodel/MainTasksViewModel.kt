package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.twisthenry8gmail.weeklyphoenix.NonNullMutableLiveData
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.tasks.TaskRepository
import com.twisthenry8gmail.weeklyphoenix.data.tasks.TaskSnapshot
import com.twisthenry8gmail.weeklyphoenix.util.ColorUtil
import com.twisthenry8gmail.weeklyphoenix.util.TaskDisplayUtil
import com.twisthenry8gmail.weeklyphoenix.util.Transitions
import com.twisthenry8gmail.weeklyphoenix.util.bundles.TaskDateBundle
import com.twisthenry8gmail.weeklyphoenix.view.main.MainLayout
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

class MainTasksViewModel(private val taskRepository: TaskRepository) : NavigatorViewModel() {

    val currentMonth = NonNullMutableLiveData(YearMonth.now())

    val tasks = currentMonth.switchMap {

        val from = LocalDate.of(it.year, it.month, 1)
        val to = it.atEndOfMonth()

        taskRepository.getLimitedSnapshotsBetween(
            MAX_TASKS_DISPLAY,
            from,
            to
        )
    }

    fun onLayoutSelected(layout: MainLayout) {

        when (layout) {

            MainLayout.DEFAULT -> {

                navigateTo(R.id.action_global_fragmentMain)
            }

            MainLayout.TASKS -> {
            }

            MainLayout.GOALS -> {
            }
        }
    }

    fun onMonthSelected(month: Month) {

        if (currentMonth.value.month != month) {

            currentMonth.value = currentMonth.value.withMonth(month.value)
        }
    }

    fun onYearSelected(year: Int) {

        if (currentMonth.value.year != year) {

            currentMonth.value = currentMonth.value.withYear(year)
        }
    }

    fun onAdd(snapshot: TaskSnapshot) {

        navigateTo(R.id.action_global_fragmentAddTask, TaskDateBundle(snapshot.date))
    }

    fun onClick(snapshot: TaskSnapshot, view: View) {

        navigateTo(
            R.id.action_fragmentMainTasks_to_fragmentViewTaskDay,
            TaskDateBundle(snapshot.date).apply {

                putInt(
                    Transitions.CONTAINER_COLOR, ColorUtil.compositeColorWithBackground(
                        view.context,
                        TaskDisplayUtil.getCardBackgroundColor(view.context, snapshot)
                    )
                )
            },
            FragmentNavigatorExtras(view to view.transitionName)
        )
    }

    companion object {

        const val MAX_TASKS_DISPLAY = 5
    }

    class Factory(private val taskRepository: TaskRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            @Suppress("UNCHECKED_CAST")
            return MainTasksViewModel(taskRepository) as T
        }
    }
}