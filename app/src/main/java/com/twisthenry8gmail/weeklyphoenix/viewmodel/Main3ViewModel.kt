package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import com.twisthenry8gmail.weeklyphoenix.*
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.data.TaskDay
import com.twisthenry8gmail.weeklyphoenix.data.TaskRepository
import com.twisthenry8gmail.weeklyphoenix.util.GoalComparator
import com.twisthenry8gmail.weeklyphoenix.util.GreetingGenerator
import com.twisthenry8gmail.weeklyphoenix.util.bundles.GoalIdBundle
import com.twisthenry8gmail.weeklyphoenix.util.bundles.TaskDateBundle
import com.twisthenry8gmail.weeklyphoenix.view.add.FragmentAddTask
import com.twisthenry8gmail.weeklyphoenix.view.main.GoalAdapter2Diff
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Main3ViewModel(
    private val androidResources: Resources,
    private val mainRepository: MainRepository,
    private val goalRepository: GoalRepository,
    private val taskRepository: TaskRepository
) : NavigatorViewModel() {

    private val _goals = NonNullMutableLiveData<List<Goal?>>(listOf(null))
    val goals: NonNullLiveData<List<Goal?>>
        get() = _goals

    private val _goalsDiffData = MediatorLiveData<DiffUtil.DiffResult>().apply {

        addSource(goalRepository.getAll()) { newGoals ->

            val oldGoals = _goals.value

            viewModelScope.launch(Dispatchers.Default) {

                val sortedNewGoals = newGoals.sortedWith(GoalComparator()).plus(null as Goal?)
                val diff = DiffUtil.calculateDiff(GoalAdapter2Diff(oldGoals, sortedNewGoals))

                withContext(Dispatchers.Main) {

                    _goals.value = sortedNewGoals
                    value = diff
                }
            }
        }
    }
    val goalsDiffData: LiveData<DiffUtil.DiffResult>
        get() = _goalsDiffData

    private var _taskDays = taskRepository.getAllDays()
    val taskDays: LiveData<List<TaskDay>>
        get() = _taskDays

    val title = GreetingGenerator.generate(androidResources)

    init {

        if (goalRepository.isTiming()) {

            navigateTo(R.id.action_fragmentMain_to_fragmentGoalTimer)
        } else if (mainRepository.isFirstTime()) {

            navigateTo(R.id.action_fragmentMain_to_fragmentOnboarding)
        }
    }

    fun onLayoutButtonClick() {

        // TODO
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

    fun onAddTask(taskDay: TaskDay) {

        navigateTo(
            R.id.action_fragmentMain_to_fragmentAddTask,
            FragmentAddTask.buildArgs(taskDay.date)
        )
    }

    fun onDayClick(taskDay: TaskDay) {

        navigateTo(R.id.action_fragmentMain_to_fragmentViewTaskDay, TaskDateBundle(taskDay.date))
    }

    class Factory(
        private val androidResources: Resources,
        private val mainRepository: MainRepository,
        private val goalRepository: GoalRepository,
        private val taskRepository: TaskRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(Main3ViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return Main3ViewModel(
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