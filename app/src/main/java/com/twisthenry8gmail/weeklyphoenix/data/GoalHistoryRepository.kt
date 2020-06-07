package com.twisthenry8gmail.weeklyphoenix.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class GoalHistoryRepository(
    private val goalHistoryDao: GoalHistory.Dao
) {

    suspend fun addAllFor(goals: List<Goal>) {

        withContext(Dispatchers.Default) {

            goalHistoryDao.insert(goals.flatMap { goal ->

                val now = LocalDate.now()
                val resetDate = LocalDate.ofEpochDay(goal.resetDate)
                val periodsPassed = resetDate.until(now)[goal.reset.unit] / goal.reset.multiple
                val historyStart = resetDate.minus(goal.reset)

                List((periodsPassed + 1).toInt()) {

                    val progress = if (it == 0) goal.progress else 0
                    val target = goal.target + it * goal.increase
                    GoalHistory(
                        goal.id,
                        historyStart.plus(goal.reset * it.toLong()).toEpochDay(),
                        progress,
                        target
                    )
                }
            })
        }
    }

    suspend fun add(goalHistory: GoalHistory) {

        goalHistoryDao.insert(goalHistory)
    }

    fun getAllFor(goal: Goal, nPeriodsBack: Long): LiveData<List<GoalHistory>> {

        val queryFrom =
            LocalDate.ofEpochDay(goal.resetDate).minus(goal.reset * (nPeriodsBack + 1))
        return getAllFor(goal.id, queryFrom.toEpochDay())
    }

    fun getAllFor(goalId: Int, fromDate: Long): LiveData<List<GoalHistory>> {

        return goalHistoryDao.getAllFor(goalId, fromDate)
    }
}