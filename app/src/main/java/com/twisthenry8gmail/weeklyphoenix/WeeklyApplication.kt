package com.twisthenry8gmail.weeklyphoenix

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.twisthenry8gmail.weeklyphoenix.data.*
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalHistoryRepository
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.data.tasks.TaskRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WeeklyApplication : Application() {

    lateinit var goalRepository: GoalRepository
    lateinit var goalHistoryRepository: GoalHistoryRepository
    lateinit var taskRepository: TaskRepository

    override fun onCreate() {
        super.onCreate()

        goalRepository =
            GoalRepository(
                RoomModel.getInstance(this).goalsDao(),
                getSharedPreferences("goal_pref", Context.MODE_PRIVATE)
            )

        goalHistoryRepository =
            GoalHistoryRepository(
                RoomModel.getInstance(this).goalHistoryDao()
            )

        taskRepository =
            TaskRepository(
                RoomModel.getInstance(this).tasksDao()
            )

        GlobalScope.launch {

            resetGoals()
        }
    }

    private suspend fun resetGoals() {

        val toReset = goalRepository.getAllThatRequireReset()

        goalHistoryRepository.addAllFor(toReset)
        goalRepository.reset(toReset)
    }
}

fun Fragment.weeklyApplication(): WeeklyApplication {

    return requireActivity().application as WeeklyApplication
}