package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.util.NotificationHelper
import com.twisthenry8gmail.weeklyphoenix.view.main.GoalAdapter
import com.twisthenry8gmail.weeklyphoenix.view.main.GoalAdapterDataBuilder
import com.twisthenry8gmail.weeklyphoenix.view.main.GoalAdapterDiff
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

class MainViewModel(
    private val androidResources: Resources,
    private val goalRepository: GoalRepository,
    private val currentGoalViewModel: CurrentGoalViewModel
) : BaseViewModel() {

    init {

        viewModelScope.launch(Dispatchers.IO) {

            goalRepository.resetAllRequired()
        }

        if (goalRepository.isTiming()) {

            navigateTo(R.id.action_fragmentMain_to_fragmentGoalTimer)
        }
    }

    private var _goalAdapterData = MutableLiveData<List<GoalAdapter.Data>>()
    val goalAdapterData
        get() = _goalAdapterData

    private val _goalAdapterDiffData = MediatorLiveData<DiffUtil.DiffResult>().apply {

        val now = LocalDate.now()

        val quotes = androidResources.getStringArray(R.array.quotes)
        val header = GoalAdapter.Header(
            now.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            quotes[Random(LocalDate.now().toEpochDay()).nextInt(quotes.size)]
        )

        addSource(goalRepository.getAll()) {

            val newData = GoalAdapterDataBuilder.build(header, it)
            viewModelScope.launch(Dispatchers.IO) {

                val diffResult = DiffUtil.calculateDiff(
                    GoalAdapterDiff(
                        _goalAdapterData.value ?: listOf(), newData
                    )
                )

                _goalAdapterData.postValue(newData)
                postValue(diffResult)
            }
        }
    }
    val goalAdapterDiffData
        get() = _goalAdapterDiffData

    fun onAddGoal(context: Context, type: Goal.Type) {

        currentGoalViewModel.currentGoal.value = Goal.buildDefaultGoal(context, type)
        navigateTo(R.id.action_fragmentMain_to_fragmentAddGoalTitle)
    }

    fun onGoalClick(goal: Goal) {

        currentGoalViewModel.currentGoal.value = goal
        navigateTo(R.id.action_fragmentMain_to_fragmentViewGoal)
    }

    fun onGoalAction(context: Context, goal: Goal) {

        when (goal.type) {

            Goal.Type.COUNTED -> {

                viewModelScope.launch {

                    goalRepository.addProgress(goal.id, 1)
                }

                if (goal.progress + 1 == goal.target) {

                    currentGoalViewModel.currentGoal.value = goal
                    navigateTo(R.id.action_fragmentMain_to_fragmentGoalSuccess)
                }
            }

            Goal.Type.TIMED -> {

                startGoalTimer(context, goal.id)
                navigateTo(R.id.action_fragmentMain_to_fragmentGoalTimer)
            }
        }
    }

    private fun startGoalTimer(context: Context, goalId: Int) {

        val time = goalRepository.startTimer(goalId)
        NotificationHelper.showGoalTimerNotification(context, time)
        NotificationHelper.scheduleNextGoalTimerNotification(context, time)
    }

    class Factory(
        private val androidResources: Resources,
        private val goalRepository: GoalRepository,
        private val currentGoalViewModel: CurrentGoalViewModel
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(androidResources, goalRepository, currentGoalViewModel) as T
            }

            throw IllegalArgumentException()
        }
    }
}