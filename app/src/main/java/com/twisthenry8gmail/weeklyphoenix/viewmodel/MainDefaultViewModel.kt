package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import com.twisthenry8gmail.weeklyphoenix.*
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.MainRepository
import com.twisthenry8gmail.weeklyphoenix.data.goals.Goal
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.data.tasks.TaskRepository
import com.twisthenry8gmail.weeklyphoenix.data.tasks.TaskSnapshot
import com.twisthenry8gmail.weeklyphoenix.util.GoalComparator
import com.twisthenry8gmail.weeklyphoenix.util.bundles.GoalIdBundle
import com.twisthenry8gmail.weeklyphoenix.util.bundles.TaskDateBundle
import com.twisthenry8gmail.weeklyphoenix.view.main.GoalAdapterDiff
import com.twisthenry8gmail.weeklyphoenix.view.main.MainLayout
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainDefaultViewModel(
    private val androidResources: Resources,
    private val mainRepository: MainRepository,
    private val goalRepository: GoalRepository,
    private val taskRepository: TaskRepository
) : NavigatorViewModel() {

    val goalsDiffData = object : DiffLiveData<Goal, Goal?>(
        goalRepository.getAll(),
        viewModelScope
    ) {

        val goalsComparator = GoalComparator()

        override fun getCallback(oldData: List<Goal?>, newData: List<Goal?>): DiffUtil.Callback {

            return GoalAdapterDiff(oldData, newData)
        }

        override fun map(data: List<Goal>): List<Goal?> {

            return data.sortedWith(goalsComparator).plusElement(null)
        }
    }

    val overdueTasks = taskRepository.getOverdue(MAX_TASK_DISPLAY)

    private val _taskSnapshots: LiveData<List<TaskSnapshot>>
    val taskSnapshots: LiveData<List<TaskSnapshot>>
        get() = _taskSnapshots

    init {

        val now = LocalDate.now()
        _taskSnapshots =
            taskRepository.getLimitedSnapshotsBetween(MAX_TASK_DISPLAY, now, now.plusDays(6))

        // TODO With splash screen instead
        if (goalRepository.isTiming()) {

//            navigateTo(R.id.action_fragmentMain_to_fragmentGoalTimer)
        } else if (mainRepository.isFirstTime()) {

            navigateTo(R.id.action_fragmentMain_to_fragmentOnboarding)
        }
    }

    fun onLayoutSelected(layout: MainLayout) {

        when (layout) {

            MainLayout.TASKS -> navigateTo(R.id.action_global_fragmentMainTasks)
        }
    }

    fun onGoalClick(goal: Goal) {

        navigateTo(
            R.id.action_fragmentMain_to_fragmentViewGoal,
            GoalIdBundle(goal.id)
        )
    }

    fun onAddGoal() {

        navigateTo(R.id.action_fragmentMain_to_fragmentAddGoal)
    }

    fun onGoalMove(goal: Goal, newSortOrder: Int) {

        viewModelScope.launch {

            goalRepository.changeSortOrder(goal.id, newSortOrder)
        }
    }

    fun onAddTask(taskSnapshot: TaskSnapshot) {

        navigateTo(
            R.id.action_global_fragmentAddTask,
            TaskDateBundle(taskSnapshot.date)
        )
    }

    fun onOverdueTasksClick() {

        navigateTo(R.id.action_fragmentMain_to_fragmentOverdueTasks)
    }

    fun onTaskCardClicked(taskSnapshot: TaskSnapshot) {

        navigateTo(
            R.id.action_fragmentMain_to_fragmentViewTaskDay,
            TaskDateBundle(taskSnapshot.date)
        )
    }

    companion object {

        const val MAX_TASK_DISPLAY = 3
    }

    class Factory(
        private val androidResources: Resources,
        private val mainRepository: MainRepository,
        private val goalRepository: GoalRepository,
        private val taskRepository: TaskRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(MainDefaultViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return MainDefaultViewModel(
                    androidResources,
                    mainRepository,
                    goalRepository,
                    taskRepository
                ) as T
            }

            throw IllegalArgumentException()
        }
    }
}