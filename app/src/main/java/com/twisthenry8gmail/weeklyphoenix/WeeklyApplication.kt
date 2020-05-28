package com.twisthenry8gmail.weeklyphoenix

import android.app.Application
import android.content.Context
import android.graphics.Color
import androidx.fragment.app.Fragment
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.data.GoalHistoryRepository
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.data.RoomModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors

class WeeklyApplication : Application() {

    lateinit var goalRepository: GoalRepository
    lateinit var goalHistoryRepository: GoalHistoryRepository

    override fun onCreate() {
        super.onCreate()

        goalRepository = GoalRepository(
            RoomModel.getInstance(this).goalsDao(),
            getSharedPreferences("goal_pref", Context.MODE_PRIVATE)
        )

        goalHistoryRepository = GoalHistoryRepository(RoomModel.getInstance(this).goalHistoryDao())

        GlobalScope.launch {

            resetGoals()
        }
    }

    private suspend fun resetGoals() {

        val now = LocalDate.now()
        val testGoal =
            Goal(
                0,
                Goal.Type.COUNTED,
                "Graph test goal",
                2,
                10,
                Goal.Reset(7, ChronoUnit.DAYS),
                now.toEpochDay(),
                0,
                false,
                now.minusDays(7).toEpochDay(),
                -1,
                Color.RED
            )
        goalRepository.add(testGoal)

        val toReset = goalRepository.getAllThatRequireReset()

        goalHistoryRepository.addAll(toReset)
        goalRepository.reset(toReset)
    }
}

fun Fragment.weeklyApplication(): WeeklyApplication {

    return requireActivity().application as WeeklyApplication
}