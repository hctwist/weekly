package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import com.twisthenry8gmail.weeklyphoenix.DiffLiveData
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.MainRepository
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalSnapshot
import com.twisthenry8gmail.weeklyphoenix.data.tasks.TaskRepository
import com.twisthenry8gmail.weeklyphoenix.data.tasks.TaskSnapshot
import com.twisthenry8gmail.weeklyphoenix.util.ColorUtil
import com.twisthenry8gmail.weeklyphoenix.util.GoalComparator
import com.twisthenry8gmail.weeklyphoenix.util.TaskDisplayUtil
import com.twisthenry8gmail.weeklyphoenix.util.Transitions
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

    val goalsDiffData = object : DiffLiveData<GoalSnapshot, GoalSnapshot?>(
        goalRepository.getSnapshots(),
        viewModelScope
    ) {

        val goalsComparator = GoalComparator()

        override fun getCallback(
            oldData: List<GoalSnapshot?>,
            newData: List<GoalSnapshot?>
        ): DiffUtil.Callback {

            return GoalAdapterDiff(oldData, newData)
        }

        override fun map(data: List<GoalSnapshot>): List<GoalSnapshot?> {

            if (data.isEmpty()) return data
            return data.sortedWith(goalsComparator).plusElement(null)
        }
    }

    val hasAddedTask = taskRepository.hasAddedTask()

    val overdueTasks = taskRepository.getOverdue(MAX_TASK_DISPLAY)

    private val _taskSnapshots: LiveData<List<TaskSnapshot>>
    val taskSnapshots: LiveData<List<TaskSnapshot>>
        get() = _taskSnapshots

    private val _showingMenu = MutableLiveData(Event(false))
    val showingMenu: LiveData<Event<Boolean>>
        get() = _showingMenu

    init {

        val now = LocalDate.now()
        _taskSnapshots =
            taskRepository.getLimitedSnapshotsBetween(MAX_TASK_DISPLAY, now, now.plusDays(6))
    }

    fun onShowMenu() {

        _showingMenu.value = Event(true)
    }

    fun onLayoutSelected(layout: MainLayout) {

        when (layout) {

            MainLayout.TASKS -> navigateTo(R.id.action_global_fragmentMainTasks)
        }
    }

    fun onGoalClick(goal: GoalSnapshot, view: View) {

        navigateTo(
            R.id.action_fragmentMain_to_fragmentViewGoal,
            GoalIdBundle(goal.id).apply {

                putInt(
                    Transitions.CONTAINER_COLOR,
                    ColorUtil.compositeColorWithBackground(
                        view.context,
                        ColorUtil.lightenColor(goal.color)
                    )
                )
            },
            FragmentNavigatorExtras(view to view.transitionName)
        )
    }

    fun onAddGoal() {

        navigateTo(R.id.action_fragmentMain_to_fragmentAddGoal)
    }

    fun onGoalMove(goal: GoalSnapshot, newSortOrder: Int) {

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

    fun onOverdueTasksClick(view: View) {

        navigateTo(R.id.action_fragmentMain_to_fragmentOverdueTasks, Bundle().apply {

            putInt(Transitions.CONTAINER_COLOR, view.context.getColor(R.color.tasks_card_overdue))
        }, FragmentNavigatorExtras(view to view.transitionName))
    }

    fun onTaskCardClicked(taskSnapshot: TaskSnapshot, view: View) {

        navigateTo(
            R.id.action_fragmentMain_to_fragmentViewTaskDay,
            TaskDateBundle(taskSnapshot.date).apply {

                putInt(
                    Transitions.CONTAINER_COLOR,
                    ColorUtil.compositeColorWithBackground(
                        view.context,
                        TaskDisplayUtil.getCardBackgroundColor(view.context, taskSnapshot)
                    )
                )
            },
            FragmentNavigatorExtras(view to view.transitionName)
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