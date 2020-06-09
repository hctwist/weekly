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

    // TODO Data binding here
    val goal = goalRepository.getTimingGoal()
    val progressUpdate = MutableLiveData<ProgressUpdate>()

    private val startTime = goalRepository.getTimingGoalStartTime()
    private val _durationSeconds = MutableLiveData((System.currentTimeMillis() - startTime) / 1000)
    val durationSeconds: LiveData<Long>
        get() = _durationSeconds

    private val updateHandler = Handler()
    private val updateRunnable = Runnable {

        _durationSeconds.value = (System.currentTimeMillis() - startTime) / 1000
        postDurationUpdate()
    }

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
                progressUpdate.value =
                    ProgressUpdate(it, it.withProgressIncrement(progressIncrement))
            }
        }
    }

    override fun onCleared() {

        updateHandler.removeCallbacks(updateRunnable)
    }

    class ProgressUpdate(val originalGoal: Goal, val updatedGoal: Goal)

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