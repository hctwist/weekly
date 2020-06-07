package com.twisthenry8gmail.weeklyphoenix

import android.app.Application
import android.content.Context
import android.graphics.Color
import androidx.fragment.app.Fragment
import com.twisthenry8gmail.weeklyphoenix.data.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month

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

        val test = false

        if (test) {
            val startDate = LocalDate.of(2020, Month.MAY, 4)
            val reset = Goal.ResetPreset.WEEKLY.toReset()
            val testGoal = Goal(
                888,
                Goal.Type.COUNTED,
                "New test goal",
                5,
                6,
                reset,
                Goal.getResetDateFrom(startDate, reset),
                2,
                false,
                startDate.toEpochDay(),
                -1,
                getColor(R.color.color_primary)
            )
            goalRepository.add(testGoal)
        }

        val test2 = true

        if(test2) {

            goalRepository.forceZeroProgress()
        }

        val toReset = goalRepository.getAllThatRequireReset()

        goalHistoryRepository.addAllFor(toReset)
        goalRepository.reset(toReset)
    }
}

fun Fragment.weeklyApplication(): WeeklyApplication {

    return requireActivity().application as WeeklyApplication
}