package com.twisthenry8gmail.weeklyphoenix

import android.app.Application
import android.content.Context
import android.graphics.Color
import androidx.fragment.app.Fragment
import com.twisthenry8gmail.weeklyphoenix.data.*
import com.twisthenry8gmail.weeklyphoenix.util.GoalPropertyUtil
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

        val add = false

        if (add) {

            val testId = 88
            val now = LocalDate.now()
            val startDate = now.minusDays(35)
            val testGoal =
                Goal(
                    testId,
                    Goal.Type.COUNTED,
                    "Graph test goal",
                    2,
                    10,
                    Goal.Reset(7, ChronoUnit.DAYS),
                    now.plusDays(7).toEpochDay(),
                    0,
                    false,
                    startDate.toEpochDay(),
                    -1,
                    Color.RED
                )
            goalRepository.add(testGoal)

            val d1 = now.minusDays(7)
            val d2 = d1.minusDays(7)
            val d3 = d2.minusDays(7)

            val h1 =
                GoalHistory(testGoal.id, d1.toEpochDay(),1, 10)
            val h2 =
                GoalHistory(testGoal.id, d2.toEpochDay(),2, 10)
            val h3 =
                GoalHistory(testGoal.id, d3.toEpochDay(), 1, 10)

            goalHistoryRepository.add(h1)
            goalHistoryRepository.add(h2)
            goalHistoryRepository.add(h3)
        }

        val toReset = goalRepository.getAllThatRequireReset()

        goalHistoryRepository.addAll(toReset)
        goalRepository.reset(toReset)
    }
}

fun Fragment.weeklyApplication(): WeeklyApplication {

    return requireActivity().application as WeeklyApplication
}