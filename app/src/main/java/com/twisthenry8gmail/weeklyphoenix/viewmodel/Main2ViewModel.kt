package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import com.twisthenry8gmail.weeklyphoenix.*
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.util.GoalComparator
import com.twisthenry8gmail.weeklyphoenix.util.NotificationHelper
import com.twisthenry8gmail.weeklyphoenix.view.main.GoalAdapter2Diff
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.random.Random

class Main2ViewModel(
    private val androidResources: Resources,
    private val mainRepository: MainRepository,
    private val goalRepository: GoalRepository
) : NavigatorViewModel() {

    private val _goals = NonNullMutableLiveData<List<Goal>>(listOf())
    val goals: NonNullLiveData<List<Goal>>
        get() = _goals

    private val _goalsDiffData = MediatorLiveData<DiffUtil.DiffResult>().apply {

        addSource(goalRepository.getAll()) { newGoals ->

            val oldGoals = _goals.value

            viewModelScope.launch(Dispatchers.Default) {

                val sortedNewGoals = newGoals.sortedWith(GoalComparator())

                withContext(Dispatchers.Main) {

                    _goals.value = sortedNewGoals
                    value = DiffUtil.calculateDiff(GoalAdapter2Diff(oldGoals, sortedNewGoals))
                }
            }
        }
    }
    val goalsDiffData: LiveData<DiffUtil.DiffResult>
        get() = _goalsDiffData

    private val _page = NonNullMutableLiveData(Event(0))
    val page: NonNullLiveData<Event<Int>>
        get() = _page

    private val _goalSuccessEvent = MutableLiveData<Event<Goal>>()
    val goalSuccessEvent: LiveData<Event<Goal>>
        get() = _goalSuccessEvent

    val title: String
    val subtitle: String

    init {

        if (goalRepository.isTiming()) {

            navigateTo(R.id.action_fragmentMain_to_fragmentGoalTimer)
        } else if (mainRepository.isFirstTime()) {

            navigateTo(R.id.action_fragmentMain_to_fragmentOnboarding)
        }

        val now = LocalDate.now()
        title = now.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val quotes = androidResources.getStringArray(R.array.quotes)
        subtitle = quotes[Random(LocalDate.now().toEpochDay()).nextInt(quotes.size)]
    }

    fun onPageSelected(page: Int) {

        _page.value = HandledEvent(page)
    }

    fun onShuffle() {

        _page.value = Event(Random.nextInt(_goals.value.size))
    }

    fun onAddGoal() {

        navigateTo(R.id.action_fragmentMain_to_fragmentAddGoalTitle)
    }

    fun onGoalClick(goal: Goal) {

        navigateTo(R.id.action_fragmentMain_to_fragmentViewGoal, GoalIdBundle(goal.id))
    }

    fun onGoalAction(context: Context, goal: Goal, card: View) {

        when (goal.type) {

            Goal.Type.COUNTED -> {

                viewModelScope.launch {

                    goalRepository.addProgress(goal.id, 1)
                }

                if (goal.progress + 1 == goal.target || true) {

                    card.transitionName = "card"
                    navigateTo(
                        R.id.action_fragmentMain_to_fragmentGoalSuccess,
                        GoalIdBundle(goal.id),
                        FragmentNavigatorExtras(card to "card")
                    )
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
        private val mainRepository: MainRepository,
        private val goalRepository: GoalRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(Main2ViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return Main2ViewModel(
                    androidResources,
                    mainRepository,
                    goalRepository
                ) as T
            }

            throw IllegalArgumentException()
        }
    }
}