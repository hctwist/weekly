package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.Context
import android.os.Handler
import androidx.lifecycle.*
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.HandledEvent
import com.twisthenry8gmail.weeklyphoenix.NonNullMutableLiveData
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.util.GoalTimerUtil
import com.twisthenry8gmail.weeklyphoenix.util.NotificationHelper
import com.twisthenry8gmail.weeklyphoenix.util.bundles.GoalIdBundle
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel
import kotlinx.coroutines.launch

class GoalTimerViewModel(private val goalRepository: GoalRepository) : NavigatorViewModel() {

    val goal = goalRepository.getTimingGoal()

    private val startTime = goalRepository.getTimingGoalStartTime()
    private val _durationSeconds = MutableLiveData((System.currentTimeMillis() - startTime) / 1000)
    val durationSeconds: LiveData<Long>
        get() = _durationSeconds

    private val updateHandler = Handler()
    private val updateRunnable = Runnable {

        _durationSeconds.value = (System.currentTimeMillis() - startTime) / 1000
        postDurationUpdate()
    }

    private val _timingStopped = NonNullMutableLiveData<Event<Boolean>>(HandledEvent(false))
    val timingStopped: LiveData<Event<Boolean>>
        get() = _timingStopped

    init {

        updateHandler.postDelayed(updateRunnable, GoalTimerUtil.calculateScheduleOffset(startTime))
    }

    private fun postDurationUpdate() {

        updateHandler.postDelayed(updateRunnable, 60 * 1000)
    }

    fun stopTimer(context: Context) {

        val progressIncrement = (System.currentTimeMillis() - startTime) / 1000

        updateHandler.removeCallbacks(updateRunnable)
        goalRepository.stopTimer()
        NotificationHelper.cancelGoalTimerNotification(context)

        viewModelScope.launch {

            goal.value?.let {

                goalRepository.addProgress(it.id, progressIncrement)
                _timingStopped.value = Event(true)
            }
        }
    }

    fun onDone() {

        goal.value?.let {

            navigateTo(R.id.action_fragmentGoalTimer_to_fragmentViewGoal, GoalIdBundle(it.id))
        }
    }

    override fun onCleared() {

        updateHandler.removeCallbacks(updateRunnable)
    }

    class Factory(private val goalRepository: GoalRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(GoalTimerViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return GoalTimerViewModel(goalRepository) as T
            }

            throw IllegalArgumentException()
        }
    }
}