package com.twisthenry8gmail.weeklyphoenix.data

import androidx.lifecycle.LiveData
import java.time.LocalDate

// TODO Are these suspend functions being run on the main thread by accident? Do I need to use Dispatchers.IO?
class GoalHistoryRepository(
    private val goalHistoryDao: GoalHistory.Dao
) {

    suspend fun addAll(goals: List<Goal>) {

        goalHistoryDao.insert(goals.map {

            val endDate = LocalDate.ofEpochDay(it.resetDate)
            val startDate = endDate.minus(it.reset.multiple, it.reset.unit)

            GoalHistory(startDate.toEpochDay(), endDate.toEpochDay(), it.id, it.progress, it.target)
        })
    }

    suspend fun add(goalHistory: GoalHistory) {

        goalHistoryDao.insert(goalHistory)
    }

    fun getAllFor(goalId: Int): LiveData<List<GoalHistory>> {

        return goalHistoryDao.getAllFor(goalId)
    }
}