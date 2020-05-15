package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.Context
import android.os.Handler
import androidx.lifecycle.*
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.util.GoalTimerUtil
import com.twisthenry8gmail.weeklyphoenix.util.NotificationHelper
import kotlinx.coroutines.launch

class GoalTimerViewModel(private val goalRepository: GoalRepository) : ViewModel() {

    val name = goalRepository.getTimingGoalName()!!
    val progressUpdate = MutableLiveData<ProgressUpdate>()

    private val startTime = goalRepository.getTimingGoalStartTime()
    private val duration = MutableLiveData<Long>().apply {

        value = System.currentTimeMillis() - startTime
    }

    private val updateHandler = Handler()
    private val updateRunnable = Runnable {

        duration.value = System.currentTimeMillis() - startTime
        postDurationUpdate()
    }

    private fun postDurationUpdate() {

        updateHandler.postDelayed(updateRunnable, 60 * 1000)
    }

    fun getDuration(): LiveData<Long> {

        updateHandler.removeCallbacks(updateRunnable)
        updateHandler.postDelayed(updateRunnable, GoalTimerUtil.calculateScheduleOffset(startTime))

        return duration
    }

    fun stopTimer(context: Context) {

        val progressIncrement = (System.currentTimeMillis() - startTime) / 1000

        updateHandler.removeCallbacks(updateRunnable)
        goalRepository.stopTimer()
        NotificationHelper.cancelGoalTimerNotificationAlarm(context)
        NotificationHelper.cancelGoalTimerNotification(context)

        viewModelScope.launch {

            val goal = goalRepository.find(name)
            val oldProgress = goal.progress

            goal.progress += progressIncrement
            goalRepository.updateGoalProgress(name, goal.progress)

            progressUpdate.value = ProgressUpdate(oldProgress, goal)
        }
    }

    override fun onCleared() {

        updateHandler.removeCallbacks(updateRunnable)
    }

    class ProgressUpdate(val oldProgress: Long, val updatedGoal: Goal)

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